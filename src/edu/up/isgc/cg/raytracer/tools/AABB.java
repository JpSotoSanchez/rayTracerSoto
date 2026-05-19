package edu.up.isgc.cg.raytracer.tools;

import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;

public class AABB {
    public final Vector3D min;
    public final Vector3D max;

    private static final double EPSILON = 1e-8;

    public AABB(Vector3D min, Vector3D max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Robust ray-box intersection using the slab method.
     * Accepts tMin and tMax for greater flexibility (e.g., BVH traversal).
     */
    public boolean intersect(Ray ray, double tMin, double tMax) {
        for (int axis = 0; axis < 3; axis++) {
            double origin = ray.getOrigin().get(axis);
            double direction = ray.getDirection().get(axis);

            if (Math.abs(direction) < EPSILON) {
                // Ray is parallel to slab
                if (origin < min.get(axis) || origin > max.get(axis)) {
                    return false;
                }
            } else {
                double invD = 1.0 / direction;
                double t0 = (min.get(axis) - origin) * invD;
                double t1 = (max.get(axis) - origin) * invD;

                if (invD < 0.0) {
                    // Swap t0 and t1
                    double temp = t0;
                    t0 = t1;
                    t1 = temp;
                }

                tMin = Math.max(t0, tMin);
                tMax = Math.min(t1, tMax);

                if (tMax <= tMin) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Overloaded version for simple use with default tMin and tMax.
     */
    public boolean intersect(Ray ray) {
        return intersect(ray, 0.001, Double.POSITIVE_INFINITY);
    }

    /**
     * Returns the center point of the AABB.
     */
    public Vector3D getCentroid() {
        return Vector3D.scaleVector(Vector3D.add(min, max), 0.5);
    }

    /**
     * Creates a new AABB that encompasses both input AABBs.
     */
    public static AABB surroundingBox(AABB b0, AABB b1) {
        Vector3D small = Vector3D.min(b0.min, b1.min);
        Vector3D big = Vector3D.max(b0.max, b1.max);
        return new AABB(small, big);
    }
}
