package edu.up.isgc.cg.raytracer;

public class Vector3D {
    private static final Vector3D ZERO = new Vector3D(0.0, 0.0, 0.0);
    private double x, y, z;

    public Vector3D(double x, double y, double z){
        setX(x);
        setY(y);
        setZ(z);
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3D clone(){
        return new Vector3D(getX(), getY(), getZ());
    }

    public static Vector3D ZERO(){
        return ZERO.clone();
    }

    @Override
    public String toString(){
        return "Vector3D{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", z=" + getZ() +
                "}";
    }

    public static double dotProduct(Vector3D vectorA, Vector3D vectorB){
        return (vectorA.getX() * vectorB.getX()) + (vectorA.getY() * vectorB.getY()) + (vectorA.getZ() * vectorB.getZ());
    }

    public static Vector3D crossProduct(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D((vectorA.getY() * vectorB.getZ()) - (vectorA.getZ() * vectorB.getY()),
                (vectorA.getZ() * vectorB.getX()) - (vectorA.getX() * vectorB.getZ()),
                (vectorA.getX() * vectorB.getY()) - (vectorA.getY() * vectorB.getX()));
    }

    public static double magnitude (Vector3D vectorA){
        return Math.sqrt(dotProduct(vectorA, vectorA));
    }

    public static Vector3D add(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D(vectorA.getX() + vectorB.getX(), vectorA.getY() + vectorB.getY(), vectorA.getZ() + vectorB.getZ());
    }

    public static Vector3D substract(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D(vectorA.getX() - vectorB.getX(), vectorA.getY() - vectorB.getY(), vectorA.getZ() - vectorB.getZ());
    }

    public static Vector3D normalize(Vector3D vectorA){
        double mag = Vector3D.magnitude(vectorA);
        return new Vector3D(vectorA.getX() / mag, vectorA.getY() / mag, vectorA.getZ() / mag);
    }

    public static Vector3D scalarMultiplication(Vector3D vectorA, double scalar){
        return new Vector3D(vectorA.getX() * scalar, vectorA.getY() * scalar, vectorA.getZ() * scalar);
    }

    public static Vector3D rotateVector(Vector3D point, double rotationX, double rotationY, double rotationZ){
        
        Vector3D point2 = new Vector3D(0, 0, 0);

        rotationX = Math.toRadians(rotationX);
        rotationY = Math.toRadians(rotationY);
        rotationZ = Math.toRadians(rotationZ);

        double cosX = Math.cos(rotationX), sinX = Math.sin(rotationX);
        double cosY = Math.cos(rotationY), sinY = Math.sin(rotationY);
        double cosZ = Math.cos(rotationZ), sinZ = Math.sin(rotationZ);
        
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        // Rotation in X
        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;
        y = y1;
        z = z1;

        // Rotation in Y
        double x2 = x * cosY + z * sinY;
        double z2 = -x * sinY + z * cosY;
        x = x2;
        z = z2;

        // Rotation in Z
        double x3 = x * cosZ - y * sinZ;
        double y3 = x * sinZ + y * cosZ;
        x = x3;
        y = y3;

        point2.setX(x);
        point2.setY(y);
        point2.setZ(z);
        
        return point2;
    }

    public static Vector3D scaleVector(Vector3D point, double scale){
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        x *= scale;
        y *= scale;
        z *= scale;

        return new Vector3D(x, y, z);
    }

    public static Vector3D refract(Vector3D incident, Vector3D normal, double n1, double n2) {
        Vector3D v = Vector3D.normalize(incident);
        Vector3D n = Vector3D.normalize(normal);
        double eta = n1 / n2;
        double cosi = -Vector3D.dotProduct(v, n);
        double k = 1 - eta * eta * (1 - cosi * cosi);

        if (k < 0) {
            return null;
        } else {
            return Vector3D.add(
                Vector3D.scalarMultiplication(v, eta),
                Vector3D.scalarMultiplication(n, eta * cosi - Math.sqrt(k))
            );
        }
    }



    public double get(int axis) {
        switch (axis) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            default: throw new IllegalArgumentException("Invalid axis");
        }
    }

    public Vector3D min(Vector3D other) {
        return new Vector3D(Math.min(this.x, other.x), Math.min(this.y, other.y), Math.min(this.z, other.z));
    }

    public Vector3D max(Vector3D other) {
        return new Vector3D(Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z));
    }

    public static Vector3D min(Vector3D a, Vector3D b) {
        return new Vector3D(
            Math.min(a.getX(), b.getX()),
            Math.min(a.getY(), b.getY()),
            Math.min(a.getZ(), b.getZ())
        );
    }
    
    public static Vector3D max(Vector3D a, Vector3D b) {
        return new Vector3D(
            Math.max(a.getX(), b.getX()),
            Math.max(a.getY(), b.getY()),
            Math.max(a.getZ(), b.getZ())
        );
    }

}
