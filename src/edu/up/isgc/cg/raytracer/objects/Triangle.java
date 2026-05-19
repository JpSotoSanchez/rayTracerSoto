package edu.up.isgc.cg.raytracer.objects;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.tools.AABB;

public class Triangle extends Object3D {
    public static final double EPSILON = 0.0000000000001;
    private Vector3D position; 
    private Vector3D[] vertices;
    private Vector3D[] normals;
    private int smoothingGroup = 0;

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Material material) {
        super(null, material);
        setVertices(v0, v1, v2);
        setCentroid();
        setNormals(null);
    }

    public Triangle(Vector3D[] vertices, Vector3D[] normals, Material material) {
        super(null, material);
        if(vertices.length == 3){
            setVertices(vertices[0], vertices[1], vertices[2]);
        } else {
            setVertices(Vector3D.ZERO(),Vector3D.ZERO(),Vector3D.ZERO());
        }
        setPosition(getCentroid());
        setNormals(normals);
    }

    public Vector3D[] getVertices() {
        return vertices;
    }

    private void setVertices(Vector3D[] vertices) {
        this.vertices = vertices;
    }

    public void setVertices(Vector3D v0, Vector3D v1, Vector3D v2) {
        setVertices(new Vector3D[]{v0, v1, v2});
    }

     public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public int getSmoothingGroup() {
        return smoothingGroup;
    }

    public void setSmoothingGroup(int smoothingGroup) {
        this.smoothingGroup = smoothingGroup;
    }

    public Vector3D getNormal(){
        Vector3D normal = Vector3D.ZERO();
        Vector3D[] normals = this.normals;

        if(normals ==null) {
            Vector3D[] vertices = getVertices();
            Vector3D v = Vector3D.substract(vertices[1], vertices[0]);
            Vector3D w = Vector3D.substract(vertices[0], vertices[2]);
            normal = Vector3D.normalize(Vector3D.crossProduct(v, w));
        } else{
            for(int i = 0; i < normals.length; i++){
                normal.setX(normal.getX() + normals[i].getX());
                normal.setY(normal.getY() + normals[i].getY());
                normal.setZ(normal.getZ() + normals[i].getZ());
            }
            normal.setX(normal.getX() / normals.length);
            normal.setY(normal.getY() / normals.length);
            normal.setZ(normal.getZ() / normals.length);
        }
        return normal;
    }

    public Vector3D[] getNormals() {
        if(normals == null) {
            Vector3D normal = getNormal();
            setNormals(new Vector3D[]{normal, normal, normal});
        }
        return normals;
    }

    public void setNormals(Vector3D[] normals) {
        this.normals = normals;
    }
    
    // FIXED: Removed the extra division by 3 in Y and Z coordinates
    public void setCentroid(){
        this.position = new Vector3D(
            (vertices[0].getX() + vertices[1].getX() + vertices[2].getX()) / 3, 
            (vertices[0].getY() + vertices[1].getY() + vertices[2].getY()) / 3, 
            (vertices[0].getZ() + vertices[1].getZ() + vertices[2].getZ()) / 3
        );
    }

    public Vector3D getCentroid(){
        return position;
    }
    
    public AABB getAABB() {
        Vector3D min = new Vector3D(
                Math.min(vertices[0].getX(), Math.min(vertices[1].getX(), vertices[2].getX())),
                Math.min(vertices[0].getY(), Math.min(vertices[1].getY(), vertices[2].getY())),
                Math.min(vertices[0].getZ(), Math.min(vertices[1].getZ(), vertices[2].getZ()))
        );
        Vector3D max = new Vector3D(
                Math.max(vertices[0].getX(), Math.max(vertices[1].getX(), vertices[2].getX())),
                Math.max(vertices[0].getY(), Math.max(vertices[1].getY(), vertices[2].getY())),
                Math.max(vertices[0].getZ(), Math.max(vertices[1].getZ(), vertices[2].getZ()))
        );
        return new AABB(min, max);
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        Intersection intersection = new Intersection(null, -1, null, null);

        Vector3D[] vert = getVertices();
        Vector3D v2v0 = Vector3D.substract(vert[2], vert[0]);
        Vector3D v1v0 = Vector3D.substract(vert[1], vert[0]);
        Vector3D vectorP = Vector3D.crossProduct(ray.getDirection(), v1v0);
        double det = Vector3D.dotProduct(v2v0, vectorP);
        
        // Check if ray is parallel to triangle
        if (Math.abs(det) < EPSILON) {
            return intersection; // No intersection
        }
        
        double invDet = 1.0 / det;
        Vector3D vectorT = Vector3D.substract(ray.getOrigin(), vert[0]);
        double u = invDet * Vector3D.dotProduct(vectorT, vectorP);

        if (!(u < 0 || u > 1)) {
            Vector3D vectorQ = Vector3D.crossProduct(vectorT, v2v0);
            double v = invDet * Vector3D.dotProduct(ray.getDirection(), vectorQ);
            if (!(v < 0 || (u + v) > (1.0 + EPSILON))) {
                double t = invDet * Vector3D.dotProduct(vectorQ, v1v0);
                
                // Only create intersection if t > 0 (ray goes forward)
                if (t > EPSILON) {
                    // Calculate intersection position
                    Vector3D intersectionPoint = Vector3D.add(ray.getOrigin(), 
                        Vector3D.scalarMultiplication(ray.getDirection(), t));
                    
                    // Calculate normal at intersection point
                    Vector3D normal;
                    if (normals != null && normals.length == 3) {
                        // Interpolate normals using barycentric coordinates
                        double w = 1.0 - u - v; // Third barycentric coordinate
                        normal = Vector3D.add(
                            Vector3D.add(
                                Vector3D.scalarMultiplication(normals[0], w),
                                Vector3D.scalarMultiplication(normals[1], u)
                            ),
                            Vector3D.scalarMultiplication(normals[2], v)
                        );
                        normal = Vector3D.normalize(normal);
                    } else {
                        // Use face normal
                        normal = getNormal();
                    }
                    
                    // Create proper intersection
                    intersection = new Intersection(intersectionPoint, t, normal, this);
                }
            }
        }

        return intersection;
    }

    public void setNormals(Vector3D vn0, Vector3D vn1, Vector3D vn2) {
        setNormals(new Vector3D[]{vn0, vn1, vn2});
    }
}