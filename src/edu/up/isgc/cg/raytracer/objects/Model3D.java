package edu.up.isgc.cg.raytracer.objects;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.tools.AABB;
import edu.up.isgc.cg.raytracer.tools.Barycentric;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model3D extends Object3D{
    private List<Triangle> triangles;
    private AABB boundingBox;


    public Model3D(Vector3D position, Triangle[] triangles, Material material) {
        super(position, material);
        setTriangles(triangles);
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
    
    public void setTriangles(Triangle[] triangles) {
        Vector3D position = getPosition();
        
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        Set<Vector3D> uniqueVertices = new HashSet<>();
        for(Triangle triangle : triangles){
            uniqueVertices.addAll(Arrays.asList(triangle.getVertices()));
        }

        for(Vector3D vertex : uniqueVertices){
            vertex.setX((vertex.getX() + position.getX()));
            vertex.setY((vertex.getY() + position.getY()));
            vertex.setZ((vertex.getZ() + position.getZ()));
            if (vertex.getX() < minX) minX = vertex.getX();
                if (vertex.getY() < minY) minY = vertex.getY();
                if (vertex.getZ() < minZ) minZ = vertex.getZ();
                if (vertex.getX() > maxX) maxX = vertex.getX();
                if (vertex.getY() > maxY) maxY = vertex.getY();
                if (vertex.getZ() > maxZ) maxZ = vertex.getZ();
        }
        Vector3D min = new Vector3D(minX, minY, minZ);
        Vector3D max = new Vector3D(maxX, maxY, maxZ);
        this.boundingBox = new AABB(min, max);
        this.triangles = Arrays.asList(triangles);
    }


   @Override
    public Intersection getIntersection(Ray ray) {
        // Prueba rápida contra AABB
        if (boundingBox != null && !boundingBox.intersect(ray)) {
            return null;
        }

        double distance = -1;
        Vector3D position = Vector3D.ZERO();
        Vector3D normal = Vector3D.ZERO();

        for(Triangle triangle : getTriangles()){
            Intersection intersection = triangle.getIntersection(ray);
            double intersectionDistance = intersection.getDistance();
            if(intersectionDistance > 0 &&
                    (intersectionDistance < distance || distance < 0)){
                distance = intersectionDistance;
                position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
                normal = Vector3D.ZERO();
                double[] uVw = Barycentric.CalculateBarycentricCoordinates(position, triangle);
                Vector3D[] normals = triangle.getNormals();
                for(int i = 0; i < uVw.length; i++){
                    normal = Vector3D.add(normal, Vector3D.scalarMultiplication(normals[i], uVw[i]));
                }
            }
        }

        if(distance == -1){
            return null;
        }

        return new Intersection(position, distance, normal, this);
    }


    @Override
    public AABB getAABB() {
        if (triangles == null || triangles.isEmpty()) {
            return null;
        }

        Vector3D min = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Vector3D max = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (Triangle triangle : triangles) {
            for (Vector3D vertex : triangle.getVertices()) {
                min = Vector3D.min(min, vertex);
                max = Vector3D.max(max, vertex);
            }
        }

        return new AABB(min, max);
    }
}
