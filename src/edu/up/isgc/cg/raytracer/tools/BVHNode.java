package edu.up.isgc.cg.raytracer.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.objects.Object3D;
import edu.up.isgc.cg.raytracer.objects.Model3D;
import edu.up.isgc.cg.raytracer.objects.Triangle;

public class BVHNode {
    private static final int MAX_TRIANGLES_PER_LEAF = 8;
    private int splitAxis;

    private AABB bounds;
    private BVHNode left;
    private BVHNode right;
    
    // For triangle-level BVH
    private List<TriangleRef> triangles;
    
    // For object-level fallback (for non-triangular objects)
    private List<Object3D> objects;

    // Reference to a triangle with its parent object
    public static class TriangleRef {
        public final Triangle triangle;
        public final Object3D parentObject;
        
        public TriangleRef(Triangle triangle, Object3D parentObject) {
            this.triangle = triangle;
            this.parentObject = parentObject;
        }
        
        public AABB getAABB() {
            return triangle.getAABB();
        }
        
        public Vector3D getCentroid() {
            return triangle.getAABB().getCentroid();
        }
        
        // Method to get intersection with proper normal interpolation
        public Intersection getIntersectionWithPhong(Ray ray) {
            Intersection basicIntersection = triangle.getIntersection(ray);
            if (basicIntersection == null || basicIntersection.getDistance() <= 0) {
                return null;
            }
            
            // If the parent is a Model3D, we need to calculate barycentric interpolation
            if (parentObject instanceof Model3D) {
                Vector3D position = basicIntersection.getPosition();
                double distance = basicIntersection.getDistance();
                
                // Calculate barycentric coordinates for normal interpolation
                double[] uVw = Barycentric.CalculateBarycentricCoordinates(position, triangle);
                Vector3D[] normals = triangle.getNormals();
                
                Vector3D interpolatedNormal = Vector3D.ZERO();
                for(int i = 0; i < uVw.length && i < normals.length; i++){
                    interpolatedNormal = Vector3D.add(interpolatedNormal, 
                        Vector3D.scalarMultiplication(normals[i], uVw[i]));
                }
                
                // Normalize the interpolated normal
                interpolatedNormal = Vector3D.normalize(interpolatedNormal);
                
                return new Intersection(position, distance, interpolatedNormal, parentObject);
            } else {
                // For non-Model3D objects, return the basic intersection but with correct parent
                return new Intersection(
                    basicIntersection.getPosition(),
                    basicIntersection.getDistance(),
                    basicIntersection.getNormal(),
                    parentObject
                );
            }
        }
    }

    public BVHNode(List<Object3D> objects) {
        // Extract all triangles from Model3D objects and keep other objects separate
        List<TriangleRef> allTriangles = new ArrayList<>();
        List<Object3D> nonTriangularObjects = new ArrayList<>();
        
        for (Object3D obj : objects) {
            if (obj instanceof Model3D) {
                Model3D model = (Model3D) obj;
                for (Triangle triangle : model.getTriangles()) {
                    allTriangles.add(new TriangleRef(triangle, obj));
                }
            } else {
                nonTriangularObjects.add(obj);
            }
        }
        
        System.out.println("Building triangle-level BVH with " + allTriangles.size() + " triangles and " + 
                          nonTriangularObjects.size() + " non-triangular objects");
        
        buildTriangleBVH(allTriangles, nonTriangularObjects);
    }
    
    private void buildTriangleBVH(List<TriangleRef> triangleRefs, List<Object3D> nonTriangularObjects) {
        // Compute bounds for all primitives
        List<AABB> allBounds = new ArrayList<>();
        
        for (TriangleRef ref : triangleRefs) {
            allBounds.add(ref.getAABB());
        }
        for (Object3D obj : nonTriangularObjects) {
            allBounds.add(obj.getAABB());
        }
        
        if (allBounds.isEmpty()) {
            return;
        }
        
        this.bounds = allBounds.get(0);
        for (int i = 1; i < allBounds.size(); i++) {
            this.bounds = AABB.surroundingBox(this.bounds, allBounds.get(i));
        }
        
        // If we have few enough primitives, make this a leaf
        int totalPrimitives = triangleRefs.size() + nonTriangularObjects.size();
        if (totalPrimitives <= MAX_TRIANGLES_PER_LEAF) {
            this.triangles = triangleRefs;
            this.objects = nonTriangularObjects;
            return;
        }
        
        // Determine split axis based on box extent
        Vector3D extent = Vector3D.substract(bounds.max, bounds.min);
        if (extent.getX() > extent.getY() && extent.getX() > extent.getZ()) {
            splitAxis = 0; // X
        } else if (extent.getY() > extent.getZ()) {
            splitAxis = 1; // Y
        } else {
            splitAxis = 2; // Z
        }
        
        // Sort triangles and objects by their centroids along the chosen axis
        triangleRefs.sort(Comparator.comparingDouble(ref -> ref.getCentroid().get(splitAxis)));
        nonTriangularObjects.sort(Comparator.comparingDouble(obj -> obj.getAABB().getCentroid().get(splitAxis)));
        
        // Split primitives into two groups
        int totalItems = triangleRefs.size() + nonTriangularObjects.size();
        int mid = totalItems / 2;
        
        List<TriangleRef> leftTriangles = new ArrayList<>();
        List<TriangleRef> rightTriangles = new ArrayList<>();
        List<Object3D> leftObjects = new ArrayList<>();
        List<Object3D> rightObjects = new ArrayList<>();
        
        // Distribute triangles
        if (mid <= triangleRefs.size()) {
            // Split is within triangles
            leftTriangles.addAll(triangleRefs.subList(0, mid));
            rightTriangles.addAll(triangleRefs.subList(mid, triangleRefs.size()));
            rightObjects.addAll(nonTriangularObjects);
        } else {
            // Split includes all triangles and some objects
            leftTriangles.addAll(triangleRefs);
            int objectSplit = mid - triangleRefs.size();
            leftObjects.addAll(nonTriangularObjects.subList(0, objectSplit));
            rightObjects.addAll(nonTriangularObjects.subList(objectSplit, nonTriangularObjects.size()));
        }
        
        // Create child nodes
        left = new BVHNode();
        left.buildTriangleBVH(leftTriangles, leftObjects);
        
        right = new BVHNode();
        right.buildTriangleBVH(rightTriangles, rightObjects);
    }
    
    // Private constructor for child nodes
    private BVHNode() {}

    public Intersection intersect(Ray ray, double tMin, double tMax) {
        if (!bounds.intersect(ray, tMin, tMax)) return null;

        // Leaf node - test against triangles and objects
        if (isLeaf()) {
            Intersection closest = null;
            double closestT = tMax;

            // Test triangles with Phong shading
            if (triangles != null) {
                for (TriangleRef ref : triangles) {
                    Intersection hit = ref.getIntersectionWithPhong(ray);
                    if (hit != null && hit.getDistance() >= tMin && hit.getDistance() < closestT) {
                        closest = hit;
                        closestT = hit.getDistance();
                    }
                }
            }
            
            // Test non-triangular objects
            if (objects != null) {
                for (Object3D obj : objects) {
                    Intersection hit = obj.getIntersection(ray);
                    if (hit != null && hit.getDistance() >= tMin && hit.getDistance() < closestT) {
                        closest = hit;
                        closestT = hit.getDistance();
                    }
                }
            }
            
            return closest;
        }

        // Internal node - recurse to children
        Intersection leftHit = left.intersect(ray, tMin, tMax);
        double rightTMax = leftHit != null ? leftHit.getDistance() : tMax;
        Intersection rightHit = right.intersect(ray, tMin, rightTMax);
        
        if (rightHit != null && (leftHit == null || rightHit.getDistance() < leftHit.getDistance())) {
            return rightHit;
        }
        return leftHit;
    }

    public Intersection intersectExcluding(Ray ray, double tMin, double tMax, Object3D excludeObject) {
        if (!bounds.intersect(ray, tMin, tMax)) return null;

        // Leaf node - test against triangles and objects
        if (isLeaf()) {
            Intersection closest = null;
            double closestT = tMax;

            // Test triangles with Phong shading
            if (triangles != null) {
                for (TriangleRef ref : triangles) {
                    if (excludeObject != null && ref.parentObject.equals(excludeObject)) {
                        continue;
                    }
                    
                    Intersection hit = ref.getIntersectionWithPhong(ray);
                    if (hit != null && hit.getDistance() >= tMin && hit.getDistance() < closestT) {
                        closest = hit;
                        closestT = hit.getDistance();
                    }
                }
            }
            
            // Test non-triangular objects
            if (objects != null) {
                for (Object3D obj : objects) {
                    if (excludeObject != null && obj.equals(excludeObject)) {
                        continue;
                    }
                    
                    Intersection hit = obj.getIntersection(ray);
                    if (hit != null && hit.getDistance() >= tMin && hit.getDistance() < closestT) {
                        closest = hit;
                        closestT = hit.getDistance();
                    }
                }
            }
            
            return closest;
        }

        // Internal node - recurse to children
        Intersection leftHit = left.intersectExcluding(ray, tMin, tMax, excludeObject);
        double rightTMax = leftHit != null ? leftHit.getDistance() : tMax;
        Intersection rightHit = right.intersectExcluding(ray, tMin, rightTMax, excludeObject);
        
        if (rightHit != null && (leftHit == null || rightHit.getDistance() < leftHit.getDistance())) {
            return rightHit;
        }
        return leftHit;
    }

    public void collectAllObjects(List<Object3D> objectList) {
        if (isLeaf()) {
            if (objects != null) {
                objectList.addAll(objects);
            }
            if (triangles != null) {
                // Add parent objects of triangles (avoid duplicates)
                for (TriangleRef ref : triangles) {
                    if (!objectList.contains(ref.parentObject)) {
                        objectList.add(ref.parentObject);
                    }
                }
            }
        } else {
            if (left != null) left.collectAllObjects(objectList);
            if (right != null) right.collectAllObjects(objectList);
        }
    }

    public List<Object3D> getAllObjects() {
        List<Object3D> allObjects = new ArrayList<>();
        collectAllObjects(allObjects);
        return allObjects;
    }

    public AABB getBounds() {
        return bounds;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public int getDepth() {
        if (isLeaf()) return 1;
        return 1 + Math.max(left.getDepth(), right.getDepth());
    }

    public int countLeaves() {
        if (isLeaf()) return 1;
        return left.countLeaves() + right.countLeaves();
    }

    public int countTotalTriangles() {
        if (isLeaf()) {
            return triangles != null ? triangles.size() : 0;
        }
        return left.countTotalTriangles() + right.countTotalTriangles();
    }
    
    public int countTotalObjects() {
        if (isLeaf()) {
            int count = objects != null ? objects.size() : 0;
            // Count unique parent objects from triangles
            if (triangles != null) {
                List<Object3D> uniqueParents = new ArrayList<>();
                for (TriangleRef ref : triangles) {
                    if (!uniqueParents.contains(ref.parentObject)) {
                        uniqueParents.add(ref.parentObject);
                    }
                }
                count += uniqueParents.size();
            }
            return count;
        }
        return left.countTotalObjects() + right.countTotalObjects();
    }

    // Statistics methods
    public void printDetailedStats() {
        System.out.println("Detailed BVH Statistics:");
        System.out.println("  - Tree depth: " + getDepth());
        System.out.println("  - Leaf nodes: " + countLeaves());
        System.out.println("  - Total triangles in BVH: " + countTotalTriangles());
        System.out.println("  - Non-triangular objects: " + countNonTriangularObjects());
        System.out.println("  - Average triangles per leaf: " + 
            String.format("%.2f", (double) countTotalTriangles() / countLeaves()));
    }
    
    private int countNonTriangularObjects() {
        if (isLeaf()) {
            return objects != null ? objects.size() : 0;
        }
        return left.countNonTriangularObjects() + right.countNonTriangularObjects();
    }

    // Getters
    public BVHNode getLeft() {
        return left;
    }

    public BVHNode getRight() {
        return right;
    }

    public List<Object3D> getObjects() {
        return objects;
    }
    
    public List<TriangleRef> getTriangles() {
        return triangles;
    }

    public int getSplitAxis() {
        return splitAxis;
    }
}