package edu.up.isgc.cg.raytracer;

import edu.up.isgc.cg.raytracer.lights.DirectionalLight;
import edu.up.isgc.cg.raytracer.lights.Light;
import edu.up.isgc.cg.raytracer.lights.PointLight;
import edu.up.isgc.cg.raytracer.objects.*;
import edu.up.isgc.cg.raytracer.sceneCreation.SceneCreator;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main raytracer class that implements a basic ray tracing algorithm
 * with support for diffuse/specular lighting, shadows, reflections,
 * and the framework for refraction.
 */
public class Raytracer {
    
    /**
     * Main entry point - creates a scene, renders it, and saves the output image.
     * Also measures and prints the rendering time.
     */
    public static void main(String[] args) {
        Date start = new Date();
        System.out.println(start);

        // Create the scene to be rendered (using predefined scene 03)
        Scene scene = SceneCreator.finalScene01();
        
        // Perform the actual ray tracing
        BufferedImage image = raytrace(scene);
        
        // Save the rendered image to disk
        File outputImage = new File("imageTest3.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("Size: " + image.getWidth() + " x " + image.getHeight());
        // Calculate and display rendering time
        Date end = new Date();
        System.out.println(start);
        System.out.println(end);
        System.out.println("Duration: " + (double)((end.getTime()-start.getTime())/1000) + "s");
    }

    /**
     * Main ray tracing function that renders the entire scene.
     * For each pixel, it casts a ray and determines the color based on intersections.
     * 
     * @param scene The scene containing camera, objects, and lights
     * @return BufferedImage containing the rendered result
     */
    

    public static BufferedImage raytrace(Scene scene) {
        // Get camera and its properties
        Camera mainCamera = scene.getCamera();
        double[] nearFarPlanes = mainCamera.getNearFarPlanes();
        
        // Create output image with camera resolution
        int width = mainCamera.getResolutionWidth();
        int height = mainCamera.getResolutionHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Create thread-safe RGB buffer
        final int[][] rgbValues = new int[width][height];
        
        // Get scene elements
        List<Object3D> objects = scene.getObjects();
        int triangledsTotal = 0;
        for (Object3D object3d : objects) {
            if (object3d instanceof Model3D){
                triangledsTotal += ((Model3D)object3d).getTriangles().size();
            }
        }
        List<Light> lights = scene.getLights();
        
        // Calculate ray positions for each pixel
        Vector3D[][] posRaytrace = mainCamera.calculatePositionsToRay();
        Vector3D cameraPos = mainCamera.getPosition();
        double cameraZ = cameraPos.getZ();
        
        // Set up depth clipping planes
        final double[] depthRange = {cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]};
        
        // Tile-based parallel processing setup
        final int TILE_SIZE = 64;
        int tileCountX = (width + TILE_SIZE - 1) / TILE_SIZE;
        int tileCountY = (height + TILE_SIZE - 1) / TILE_SIZE;
        final int totalTiles = tileCountX * tileCountY;
        final AtomicInteger completedTiles = new AtomicInteger(0);
        
        // Create thread pool based on available processors
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        // Progress tracking setup
        ScheduledExecutorService progressExecutor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> progressFuture = progressExecutor.scheduleAtFixedRate(() -> {
            int done = completedTiles.get();
            double progress = (double) done / totalTiles * 100;
            System.out.printf("\rRendering progress: %.2f%% (%d/%d tiles)", progress, done, totalTiles);
        }, 0, 1000, TimeUnit.MILLISECONDS);

        // Process tiles in parallel
        for (int tileX = 0; tileX < tileCountX; tileX++) {
            for (int tileY = 0; tileY < tileCountY; tileY++) {
                // Calculate tile boundaries
                final int startX = tileX * TILE_SIZE;
                final int endX = Math.min(startX + TILE_SIZE, width);
                final int startY = tileY * TILE_SIZE;
                final int endY = Math.min(startY + TILE_SIZE, height);
                
                executor.submit(() -> {
                    // Process each pixel in the tile
                    for (int x = startX; x < endX; x++) {
                        for (int y = startY; y < endY; y++) {
                            // Calculate world space position
                            Vector3D pixelPos = posRaytrace[x][y];
                            double worldX = pixelPos.getX() + cameraPos.getX();
                            double worldY = pixelPos.getY() + cameraPos.getY();
                            double worldZ = pixelPos.getZ() + cameraPos.getZ();
                            
                            // Create ray from camera through pixel
                            Ray ray = new Ray(cameraPos, new Vector3D(worldX, worldY, worldZ));
                            
                            // Find closest intersection
                            Intersection intersection = raycast(ray, objects, null, depthRange);
                            
                            // Calculate pixel color
                            Color color = Color.BLACK;
                            if (intersection != null) {
                                color = calculatePixelColor(intersection, scene, lights, objects, ray);
                            }
                            
                            // Store result in buffer
                            rgbValues[x][y] = color.getRGB();
                        }
                    }
                    // Update progress counter
                    completedTiles.incrementAndGet();
                });
            }
        }
        
        // Wait for all tasks to complete
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("\nRender interrupted!");
        } finally {
            // Final progress update and cleanup
            progressFuture.cancel(false);
            progressExecutor.shutdown();
            System.out.printf("\rRendering complete: 100.00%% (%d/%d tiles)%n", totalTiles, totalTiles);
        }
        
        // Transfer results to image (single-threaded for safety)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, rgbValues[x][y]);
            }
        }
        System.out.println("Total triangles: " + triangledsTotal);
        return image;
    }
    /**
     * Wrapper method for calculatePixelColor with initial depth of 0
     */
    private static Color calculatePixelColor(Intersection intersection, Scene scene, List<Light> lights, List<Object3D> objects, Ray ray) {
        return calculatePixelColor(intersection, scene, lights, objects, ray, 0);
    }
    
    /**
     * Calculates the final color of a pixel based on lighting, reflections, and refractions.
     * This is the core shading function that combines multiple lighting models.
     * 
     * @param intersection The intersection point with an object
     * @param scene The complete scene
     * @param lights List of light sources
     * @param objects List of scene objects
     * @param ray The incoming ray
     * @param depth Current recursion depth for reflections/refractions
     * @return The final calculated color
     */
    private static Color calculatePixelColor(Intersection intersection, Scene scene, List<Light> lights, List<Object3D> objects, Ray ray, int depth) {
        Color pixelColor = Color.BLACK;
        Color objColor = intersection.getObject().getMaterial().getColor();
        
        // Calculate lighting contribution from each light source
        for (Light light : lights) {
            // Diffuse lighting (Lambertian reflection)
            Color diffuseColor = calculateLightContribution(light, intersection, objects, objColor);
            pixelColor = addColor(pixelColor, diffuseColor);
            
            // Specular lighting (Phong reflection model)
            Color specularColor = calculateSpecularLight(light, intersection, scene, objects);
            pixelColor = addColor(pixelColor, specularColor);
        }
        
        // Add ambient lighting to prevent completely black areas
        Color ambientContribution = calculateAmbientLight(objColor);
        pixelColor = addColor(pixelColor, ambientContribution);
        
        // Get material properties for advanced effects
        double reflectionFactor = intersection.getObject().getMaterial().getReflectivity();
        double refractionIndex = intersection.getObject().getMaterial().getRefractivity();
        double transparencyFactor = intersection.getObject().getMaterial().getTransparency();
        
        // Handle reflections (recursive ray tracing)
        if (reflectionFactor > 0 && depth < scene.getMaxReflectionDepth()) {
            Color reflectedColor = calculateReflection(ray, intersection, scene, objects, depth + 1);
            pixelColor = blendColors(pixelColor, reflectedColor, reflectionFactor);
        }

        // Handle refractions for transparent materials
        if (refractionIndex > 1.0 && transparencyFactor > 0 && depth < scene.getMaxReflectionDepth()) {
            Color refractedColor = calculateRefraction(ray, intersection, scene, objects, depth + 1);
            pixelColor = blendColors(pixelColor, refractedColor, transparencyFactor);
        }

        return pixelColor;
    }
    
    /**
     * Calculates reflection color using the law of reflection.
     * Creates a reflected ray and recursively traces it.
     * 
     * @param ray The incoming ray
     * @param intersection The intersection point
     * @param scene The scene
     * @param objects Scene objects
     * @param depth Current recursion depth
     * @return Color from the reflected ray
     */
    private static Color calculateReflection(Ray ray, Intersection intersection, Scene scene, List<Object3D> objects, int depth) {
        Vector3D normal = intersection.getNormal();
        Vector3D rayDirection = ray.getDirection();
        
        // Normalize the incoming ray direction
        Vector3D I = Vector3D.normalize(rayDirection);
        
        // Calculate reflection vector using: R = I - 2(N⋅I)N
        double dotProduct = Vector3D.dotProduct(normal, I);
        Vector3D reflection = Vector3D.substract(I, Vector3D.scalarMultiplication(normal, 2 * dotProduct));
        reflection = Vector3D.normalize(reflection);
        
        // Create new ray origin slightly offset to avoid self-intersection
        Vector3D newOrigin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(reflection, 0.001));
        Ray reflectedRay = new Ray(newOrigin, reflection);
        
        // Find what the reflected ray hits
        Intersection reflectedIntersection = raycast(reflectedRay, objects, intersection.getObject(), null);
        
        // If nothing is hit, return black (could be skybox color instead)
        if (reflectedIntersection == null) {
            return Color.BLACK;
        }
        
        // Recursively calculate color of the reflected intersection
        return calculatePixelColor(reflectedIntersection, scene, scene.getLights(), objects, reflectedRay, depth);
    }

    /**
     * Calculates refraction color with material absorption and multiple interface handling.
     * Manages complete internal reflection, material transitions, and Beer's Law absorption.
     * 
     * @param ray Current ray being processed
     * @param intersection Intersection point data (position, normal, object)
     * @param scene Scene configuration (lights, max recursion depth)
     * @param objects All 3D objects in the scene
     * @param depth Current recursion depth (prevents infinite loops)
     * @return Color resulting from refraction calculations
     */
    private static Color calculateRefraction(Ray ray, Intersection intersection, Scene scene, List<Object3D> objects, int depth) {
        // Depth guard clause - prevents stack overflow from infinite recursion
        if (depth >= scene.getMaxReflectionDepth()) {
            return Color.BLACK;
        }
        
        // Surface normal and ray direction calculations
        Vector3D normal = intersection.getNormal();
        Vector3D incomingDirection = Vector3D.normalize(ray.getDirection());
        Object3D currentObject = intersection.getObject();
        
        // Refractive indices handling (air -> material transition)
        double n1 = 1.0; // Default assumption: coming from air
        double n2 = currentObject.getMaterial().getRefractivity();
        
        // Determine material entry/exit using dot product
        double cosI = Vector3D.dotProduct(Vector3D.scalarMultiplication(incomingDirection, -1), normal);
        boolean entering = cosI > 0;
        
        // Handle material exit case (swap indices and flip normal)
        if (!entering) {
            normal = Vector3D.scalarMultiplication(normal, -1);
            double temp = n1;
            n1 = n2;
            n2 = temp;
            cosI = -cosI;
        }
        
        // Snell's Law calculations with total internal reflection check
        double eta = n1 / n2;
        double k = 1.0 - eta * eta * (1.0 - cosI * cosI);
        
        // Total internal reflection fallback to reflection
        if (k < 0.0) {
            return calculateReflection(ray, intersection, scene, objects, depth);
        }
        
        // Calculate refracted direction vector using Snell's formula
        double cosThetaT = Math.sqrt(k);
        Vector3D refractedDirection = Vector3D.add(
            Vector3D.scalarMultiplication(incomingDirection, eta),
            Vector3D.scalarMultiplication(normal, eta * cosI - cosThetaT)
        );
        refractedDirection = Vector3D.normalize(refractedDirection);
        
        // Create offset ray to prevent self-intersection artifacts
        Vector3D offsetPoint = Vector3D.add(
            intersection.getPosition(), 
            Vector3D.scalarMultiplication(refractedDirection, 0.001) // Small epsilon offset
        );
        Ray refractedRay = new Ray(offsetPoint, refractedDirection);
        
        // Cast new ray through scene objects (excluding current when exiting)
        Intersection nextIntersection = raycast(refractedRay, objects, entering ? null : currentObject, null);
        
        // No intersection = empty transmission
        if (nextIntersection == null) {
            return Color.BLACK;
        }
        
        // Handle material transmission with absorption (entering case)
        if (entering && nextIntersection.getObject().equals(currentObject)) {
            // Beer-Lambert law application
            double distanceInMaterial = nextIntersection.getDistance();
            Color materialColor = currentObject.getMaterial().getColor();
            double absorption = Math.exp(-distanceInMaterial * 0.1); // Absorption coefficient
            
            // Recursive calculation for exit color
            Color exitColor = calculateRefraction(refractedRay, nextIntersection, scene, objects, depth + 1);
            
            // Apply wavelength-dependent absorption
            return applyMaterialAbsorption(exitColor, materialColor, absorption);
        } else {
            // Standard surface shading for new object hit
            return calculatePixelColor(nextIntersection, scene, scene.getLights(), objects, refractedRay, depth + 1);
        }
    }

    /**
     * Applies material color absorption using Beer-Lambert law approximation.
     * 
     * @param lightColor Original light color passing through material
     * @param materialColor Object's intrinsic color properties
     * @param absorption Absorption strength (0-1 range, 1=full transmission)
     * @return New color adjusted for material absorption
     */
    private static Color applyMaterialAbsorption(Color lightColor, Color materialColor, double absorption) {
        // Convert colors to normalized RGB arrays [0-1]
        double[] lightRGB = colorToNormalizedArray(lightColor);
        double[] materialRGB = colorToNormalizedArray(materialColor);
        
        // Apply per-channel absorption using material color as filter
        double[] resultRGB = new double[3];
        for (int i = 0; i < 3; i++) {
            resultRGB[i] = lightRGB[i] * Math.pow(materialRGB[i], 1.0 - absorption);
        }
        
        // Ensure color values stay within valid range
        return createClampedColor(resultRGB);
    }


    /**
     * Blends two colors based on a blending factor.
     * Used for combining base color with reflection/refraction colors.
     * 
     * @param baseColor The original color
     * @param overlayColor The color to blend in
     * @param factor Blending factor (0.0 to 1.0)
     * @return Blended color
     */
    private static Color blendColors(Color baseColor, Color overlayColor, double factor) {
        // Clamp factor to valid range
        factor = Math.max(0.0, Math.min(1.0, factor));
        
        // Linear interpolation between colors
        int r = (int) (baseColor.getRed() * (1 - factor) + overlayColor.getRed() * factor);
        int g = (int) (baseColor.getGreen() * (1 - factor) + overlayColor.getGreen() * factor);
        int b = (int) (baseColor.getBlue() * (1 - factor) + overlayColor.getBlue() * factor);
        
        // Clamp to valid color range
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        return new Color(r, g, b);
    }

    /**
     * Calculates diffuse lighting contribution using Lambertian reflection model.
     * Checks for shadows and applies light attenuation based on distance.
     * 
     * @param light The light source
     * @param intersection The intersection point
     * @param objects Scene objects (for shadow testing)
     * @param objColor The object's material color
     * @return Diffuse color contribution
     */
    private static Color calculateLightContribution(Light light, Intersection intersection, List<Object3D> objects, Color objColor) {
        // Check if point is in shadow
        if (isPointInShadow(light, intersection, objects)) {
            return Color.BLACK;
        }
        
        // Calculate angle between surface normal and light direction
        double nDotL = light.getNDotL(intersection);
        
        // Only process positive contributions (surface facing light)
        if (nDotL <= 0) {
            return Color.BLACK;
        }
        
        // Calculate light intensity with distance falloff
        double intensity = calculateLightIntensity(light, intersection, nDotL);
        
        // Skip very small contributions for performance
        if (intensity < 0.001) {
            return Color.BLACK;
        }
        
        // Apply lighting to object color
        return applyLightToColor(objColor, light.getColor(), intensity);
    }

    /**
     * Calculates specular lighting using the Phong reflection model.
     * Creates bright highlights on shiny surfaces.
     * 
     * @param light The light source
     * @param intersection The intersection point
     * @param scene The scene (for camera position)
     * @param objects Scene objects (for shadow testing)
     * @return Specular color contribution
     */
    private static Color calculateSpecularLight(Light light, Intersection intersection, Scene scene, List<Object3D> objects) {
        // Check if point is in shadow
        if (isPointInShadow(light, intersection, objects)) {
            return Color.BLACK;
        }
        
        // Calculate vectors for Phong model
        Vector3D N = intersection.getNormal();                    // Surface normal
        Vector3D L = Vector3D.normalize(Vector3D.substract(light.getPosition(), intersection.getPosition())); // Light direction
        Vector3D V = Vector3D.normalize(Vector3D.substract(scene.getCamera().getPosition(), intersection.getPosition())); // View direction
        Vector3D R = Vector3D.normalize(Vector3D.substract(Vector3D.scalarMultiplication(N, 2 * Vector3D.dotProduct(N, L)), L)); // Reflection of light

        // Get material specular properties
        double k_s = intersection.getObject().getMaterial().getSpecular();     // Specular reflectivity
        double alpha = intersection.getObject().getMaterial().getShininess();  // Shininess exponent
        
        // Skip if material has no specular properties
        if (k_s <= 0 || alpha <= 0) {
            return Color.BLACK;
        }

        // Only calculate when reflection and view vectors align
        double RdotV = Vector3D.dotProduct(R, V);
        if (RdotV <= 0) {
            return Color.BLACK;
        }
        
        // Calculate specular intensity using Phong model
        double specIntensity = k_s * Math.pow(RdotV, alpha);
        
        // Apply light intensity and distance falloff
        specIntensity *= calculateLightIntensityForSpecular(light, intersection);
        
        // Apply intensity to light color
        double[] lightColors = colorToNormalizedArray(light.getColor());
        double[] specularColor = new double[]{
            specIntensity * lightColors[0],
            specIntensity * lightColors[1],
            specIntensity * lightColors[2]
        };

        return createClampedColor(specularColor);
    }

    /**
     * Calculates light intensity for specular highlights with distance falloff.
     * Different behavior for point lights vs directional lights.
     */
    private static double calculateLightIntensityForSpecular(Light light, Intersection intersection) {
        double intensity = light.getIntensity();
        
        if (light instanceof PointLight) {
            // Apply quadratic distance falloff for point lights
            double distance = Vector3D.magnitude(Vector3D.substract(light.getPosition(), intersection.getPosition()));
            double falloff = 1.0 / (Math.pow(distance, 2));
            return intensity * falloff;
        } 
        else if (light instanceof DirectionalLight) {
            // No distance falloff for directional lights (like sun)
            return intensity;
        }
        
        return intensity;
    }

    /**
     * Determines if a point is in shadow by casting a ray towards the light source.
     * Uses shadow bias to prevent self-shadowing artifacts.
     */
    private static boolean isPointInShadow(Light light, Intersection intersection, List<Object3D> objects) {
        // Skip shadow calculation if light doesn't cast shadows
        if (!light.getAbleShadow()) {
            return false;
        }
        
        // Create ray from intersection point towards light
        Vector3D lightDirection = Vector3D.normalize(Vector3D.substract(light.getPosition(), intersection.getPosition()));
        // Apply small bias to prevent self-intersection
        Vector3D offsetPoint = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(lightDirection, 0.001));
        Ray lightRay = new Ray(offsetPoint, lightDirection);
        
        // Calculate distance to light
        double distanceObjectLight = Vector3D.magnitude(Vector3D.substract(light.getPosition(), intersection.getPosition()));
        
        // Check if any object blocks the light
        Intersection shadowIntersection = shadowCast(lightRay, objects, null);
        
        // Point is in shadow if something is hit before reaching the light
        return shadowIntersection != null && shadowIntersection.getDistance() <= distanceObjectLight;
    }

    /**
     * Calculates light intensity with distance falloff for diffuse lighting.
     * Uses physically-based quadratic falloff for point lights.
     */
    private static double calculateLightIntensity(Light light, Intersection intersection, double nDotL) {
        if (nDotL <= 0) {
            return 0.0;
        }
        
        double intensity = light.getIntensity() * nDotL;
        
        if (light instanceof PointLight) {
            // Physically correct quadratic falloff with constant term to prevent singularity
            double distance = Vector3D.magnitude(Vector3D.substract(light.getPosition(), intersection.getPosition()));
            double falloff = 1.0 / (1 + Math.pow(distance, 2));
            return intensity * falloff;
        } 
        else if (light instanceof DirectionalLight) {
            // No falloff for directional lights
            return intensity;
        }
        
        return intensity;
    }

    /**
     * Applies light color and intensity to object material color.
     * Performs component-wise multiplication of colors.
     */
    private static Color applyLightToColor(Color objColor, Color lightColor, double intensity) {
        double[] lightColors = colorToNormalizedArray(lightColor);
        double[] objColors = colorToNormalizedArray(objColor);
        
        // Multiply object color by light color and intensity
        for (int i = 0; i < objColors.length; i++) {
            objColors[i] *= intensity * lightColors[i];
        }
        
        return createClampedColor(objColors);
    }

    /**
     * Calculates ambient lighting contribution.
     * Provides base illumination to prevent completely black areas.
     * Note: There's a typo in the division (2550.0 should probably be 255.0)
     */
    private static Color calculateAmbientLight(Color objColor) {
        double[] objColors = {
            objColor.getRed() / 255.0,    
            objColor.getGreen() / 255.0,  
            objColor.getBlue() / 255.0    
        };
        
        double[] ambientColor = colorToNormalizedArray(Color.WHITE);
        double intensityAmbient = 0.01;
        
        // Apply ambient intensity
        for (int i = 0; i < objColors.length; i++) {
            objColors[i] *= intensityAmbient * ambientColor[i];
        }
        
        return createClampedColor(objColors);
    }

    /**
     * Converts Color object to normalized RGB array (0.0 to 1.0 range).
     */
    private static double[] colorToNormalizedArray(Color color) {
        return new double[] {
            color.getRed() / 255.0,
            color.getGreen() / 255.0,
            color.getBlue() / 255.0
        };
    }

    /**
     * Creates Color object from normalized RGB array, clamping values to valid range.
     */
    private static Color createClampedColor(double[] colorArray) {
        return new Color(
            (float) Math.clamp(colorArray[0], 0.0, 1.0),
            (float) Math.clamp(colorArray[1], 0.0, 1.0),
            (float) Math.clamp(colorArray[2], 0.0, 1.0)
        );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    // UTILITY METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds two colors together with proper clamping.
     * Used for combining multiple lighting contributions.
     */
    public static Color addColor(Color original, Color otherColor) {
        float red = (float) Math.clamp((original.getRed() / 255.0) + (otherColor.getRed() / 255.0), 0.0, 1.0);
        float green = (float) Math.clamp((original.getGreen() / 255.0) + (otherColor.getGreen() / 255.0), 0.0, 1.0);
        float blue = (float) Math.clamp((original.getBlue() / 255.0) + (otherColor.getBlue() / 255.0), 0.0, 1.0);
        return new Color(red, green, blue);
    }

    /**
     * Performs ray-object intersection testing with optional clipping planes.
     * Returns the closest valid intersection.
     * 
     * @param ray The ray to test
     * @param objects List of objects to test against
     * @param caster Object to exclude from testing (usually the ray origin object)
     * @param clippingPlanes Near and far clipping distances [near, far]
     * @return Closest intersection within valid range, or null if none found
     */
    public static Intersection raycast(Ray ray, List<Object3D> objects, Object3D caster, double[] clippingPlanes) {
        Intersection closestIntersection = null;

        // Test ray against all objects
        for (int i = 0; i < objects.size(); i++) {
            Object3D currObj = objects.get(i);
            
            // Skip the casting object to avoid self-intersection
            if (caster == null || !currObj.equals(caster)) {
                Intersection intersection = currObj.getIntersection(ray);
                
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    double intersectionZ = intersection.getPosition().getZ();

                    // Check if intersection is valid:
                    // - Positive distance (in front of ray origin)
                    // - Closer than previous closest
                    // - Within clipping planes (if specified)
                    if (distance >= 0 &&
                            (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                            (clippingPlanes == null || (intersectionZ >= clippingPlanes[0] && intersectionZ <= clippingPlanes[1]))) {
                        closestIntersection = intersection;
                    }
                }
            }
        }
        return closestIntersection;
    }

    /**
     * Simplified ray casting for shadow testing.
     * Similar to raycast but without clipping plane checks.
     * Used specifically for shadow ray testing.
     */
    public static Intersection shadowCast(Ray ray, List<Object3D> objects, Object3D caster) {
        Intersection closestIntersection = null;

        for (int i = 0; i < objects.size(); i++) {
            Object3D currObj = objects.get(i);
            
            // Skip the casting object
            if (caster == null || !currObj.equals(caster)) {
                Intersection intersection = currObj.getIntersection(ray);
                
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    
                    // Only need closest intersection for shadow testing
                    if (distance >= 0 && (closestIntersection == null || distance < closestIntersection.getDistance())) {
                        closestIntersection = intersection;        
                    }
                }
            }
        }
        return closestIntersection;
    }
}