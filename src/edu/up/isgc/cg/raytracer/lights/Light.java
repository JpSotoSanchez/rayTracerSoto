package edu.up.isgc.cg.raytracer.lights;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.objects.IIntersectable;
import edu.up.isgc.cg.raytracer.objects.Object3D;

import java.awt.*;

public abstract class Light implements IIntersectable {
    private Vector3D position;
    private Color color;  
    private double intensity;
    private boolean ableShadow;

    public Light(Vector3D position, Color color, double intensity, boolean ableShadow) {
        setPosition(position);
        setColor(color);
        setIntensity(intensity);
        setAbleShadow(ableShadow);
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public boolean isAbleShadow() {
        return ableShadow;
    }

    public void setAbleShadow(boolean ableShadow) {
        this.ableShadow = ableShadow;
    }

    public boolean getAbleShadow() {
        return ableShadow;
    }

    public abstract double getNDotL(Intersection intersection);


    @Override
    public Intersection getIntersection(Ray ray) {
        return new Intersection(Vector3D.ZERO(), -1, Vector3D.ZERO(), (Object3D)null);
    }
    
}
