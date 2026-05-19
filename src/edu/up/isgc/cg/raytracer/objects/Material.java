package edu.up.isgc.cg.raytracer.objects;

import java.awt.Color;

public class Material {
    private Color color;
    private double specular;  
    private double shininess;
    private double reflectivity;
    private double refractivity;
    private double transparency;
   
    public Material(Color color, double specular, double shininess, double reflectivity, double refractivity,
            double transparency) {
        this.color = color;
        this.specular = specular;
        this.shininess = shininess;
        this.reflectivity = reflectivity;
        this.refractivity = refractivity;
        this.transparency = transparency;
    }
    
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public double getSpecular() {
        return specular;
    }
    public void setSpecular(double specular) {
        this.specular = specular;
    }
    public double getShininess() {
        return shininess;
    }
    public void setShininess(double shininess) {
        this.shininess = shininess;
    }
    public double getReflectivity() {
        return reflectivity;
    }
    public void setReflectivity(double reflectivity) {
        this.reflectivity = reflectivity;
    }
    public double getRefractivity() {
        return refractivity;
    }
    public void setRefractivity(double refractivity) {
        this.refractivity = refractivity;
    }
    public double getTransparency() {
        return transparency;
    }
    public void setTransparency(double transparency) {
        this.transparency = transparency;
    } 
}

