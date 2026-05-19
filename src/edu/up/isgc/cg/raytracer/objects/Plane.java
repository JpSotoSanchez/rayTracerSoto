package edu.up.isgc.cg.raytracer.objects;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.tools.AABB;


public class Plane extends Object3D {
    private Vector3D normal;
    
    
    public Plane(Vector3D position, Vector3D normal, Material material) {
        super(position, material);
        this.normal = Vector3D.normalize(normal);
    }
    
    
    public Vector3D getNormal() {
        return normal;
    }
    
    
    public void setNormal(Vector3D normal) {
        this.normal = Vector3D.normalize(normal);
    }
    
    
    @Override
    public Intersection getIntersection(Ray ray) {
        double denominator = Vector3D.dotProduct(ray.getDirection(), this.normal);
        
        if (Math.abs(denominator) < 1e-6) {
            return null;
        }
        
        Vector3D rayToPlane = Vector3D.substract(this.getPosition(), ray.getOrigin());
        
        double t = Vector3D.dotProduct(rayToPlane, this.normal) / denominator;
        
        if (t < 0) {
            return null;
        }
        
        Vector3D intersectionPoint = Vector3D.add(ray.getOrigin(), 
            Vector3D.scalarMultiplication(ray.getDirection(), t));
        
        Vector3D normalAtIntersection = this.normal;
        if (Vector3D.dotProduct(ray.getDirection(), normalAtIntersection) > 0) {
            normalAtIntersection = Vector3D.scalarMultiplication(normalAtIntersection, -1);
        }
        
        return new Intersection(intersectionPoint, t, normalAtIntersection, this);
    }
    
    
    @Override
    public AABB getAABB() {
        double infinity = Double.POSITIVE_INFINITY;
        return new AABB(
            new Vector3D(-infinity, -infinity, -infinity),
            new Vector3D(infinity, infinity, infinity)
        );
    }
    
    
    @Override
    public String toString() {
        return "Plane{" +
                "position=" + getPosition() +
                ", normal=" + normal +
                ", material=" + getMaterial() +
                '}';
    }
}