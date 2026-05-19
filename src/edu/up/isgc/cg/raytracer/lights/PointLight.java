package edu.up.isgc.cg.raytracer.lights;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Vector3D;

import java.awt.Color;

public class PointLight extends Light {

    public PointLight(Vector3D position, Color color, double intensity, boolean ableShadow) {
        super(position, color, intensity, ableShadow);
    }

   

    @Override
    public double getNDotL(Intersection intersection) {
        Vector3D lightIntersection = Vector3D.substract(intersection.getObject().getPosition(), getPosition());
        return Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.scalarMultiplication(lightIntersection, -1.0)), 0.0);
    }
    
    
}
