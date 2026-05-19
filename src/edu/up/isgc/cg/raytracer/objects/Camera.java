package edu.up.isgc.cg.raytracer.objects;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.tools.AABB;


public class Camera extends Object3D {
    //FOV[0] = Horizontal | FOV[1] = Vertical
    private double[] fieldOfView = new double[2];
    private double defaultZ = 15.0;
    private int[] resolution = new int[2];
    private double[] nearFarPlanes = new double[2];

    public Camera(Vector3D position, double fovH, double fovV,
                  int width, int height, double nearPlane, double farPlane) {
        super(position, null);
        setFOV(fovH, fovV);
        setResolution(width, height);
        setNearFarPlanes(new double[]{nearPlane, farPlane});
    }

    public double[] getFieldOfView() {
        return fieldOfView;
    }


    public double getFOVHorizontal() {
        return fieldOfView[0];
    }

    public double getFOVVertical() {
        return fieldOfView[1];
    }

    public void setFOVHorizontal(double fovH) {
        fieldOfView[0] = fovH;
    }

    public void setFOVVertical(double fovV) {
        fieldOfView[1] = fovV;
    }

    public void setFOV(double fovH, double fovV) {
        setFOVHorizontal(fovH);
        setFOVVertical(fovV);
    }

    public double getDefaultZ() {
        return defaultZ;
    }

    public void setDefaultZ(double defaultZ) {
        this.defaultZ = defaultZ;
    }

    public int[] getResolution() {
        return resolution;
    }

    public void setResolutionWidth(int width) {
        resolution[0] = width;
    }

    public void setResolutionHeight(int height) {
        resolution[1] = height;
    }

    public void setResolution(int width, int height) {
        setResolutionWidth(width);
        setResolutionHeight(height);
    }

    public int getResolutionWidth() {
        return resolution[0];
    }

    public int getResolutionHeight() {
        return resolution[1];
    }

    

    public double[] getNearFarPlanes() {
        return nearFarPlanes;
    }

    private void setNearFarPlanes(double[] nearFarPlanes) {
        this.nearFarPlanes = nearFarPlanes;
    }


    public Vector3D[][] calculatePositionsToRay() {
        Vector3D[][] positions = new Vector3D[getResolutionWidth()][getResolutionHeight()];
        
        double fovHRadians = Math.toRadians(getFOVHorizontal());
        double fovVRadians = Math.toRadians(getFOVVertical());
        
        double anglePerPixelX = fovHRadians / getResolutionWidth();
        double anglePerPixelY = fovVRadians / getResolutionHeight();
        
        double startAngleX = -fovHRadians / 2.0;
        double startAngleY = fovVRadians / 2.0;
        
        for (int x = 0; x < getResolutionWidth(); x++) {
            for (int y = 0; y < getResolutionHeight(); y++) {
                double currentAngleX = startAngleX + (x + 0.5) * anglePerPixelX;
                double currentAngleY = startAngleY - (y + 0.5) * anglePerPixelY;
                
                double posX = defaultZ * Math.tan(currentAngleX);
                double posY = defaultZ * Math.tan(currentAngleY);
                double posZ = defaultZ;
                
                positions[x][y] = new Vector3D(posX, posY, posZ);
            }
        }
        return positions;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        return new Intersection(Vector3D.ZERO(), -1, Vector3D.ZERO(), null);
    }

    @Override
    public AABB getAABB() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAABB'");
    }
}
