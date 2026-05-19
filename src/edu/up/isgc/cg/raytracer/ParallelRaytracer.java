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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ParallelRaytracer {
    
    private static final int TILE_SIZE = 64; 
    private static final long PROGRESS_UPDATE_MS = 1000; 
    private static final double EPSILON = 1e-6; 
    
    public static void main(String[] args) {
        Date start = new Date();
        System.out.println("Inicio del renderizado: " + start);

        Scene scene = SceneCreator.finalScene01();
        BufferedImage image = raytrace(scene);
        
        File outputImage = new File("imageTest5.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
            return;
        }
        
        Date end = new Date();
        double duration = (end.getTime() - start.getTime()) / 1000.0;
        
        System.out.println("Render finished!");
        System.out.println("Resolution: " + image.getWidth() + " x " + image.getHeight());
        System.out.println("Total time: " + String.format("%.2f", duration) + "s");
        System.out.println("Image saved as: " + outputImage.getAbsolutePath());
    }

    public static BufferedImage raytrace(Scene scene) {
        Camera mainCamera = scene.getCamera();
        double[] nearFarPlanes = mainCamera.getNearFarPlanes();
        
        int width = mainCamera.getResolutionWidth();
        int height = mainCamera.getResolutionHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        final int[] rgbBuffer = new int[width * height];
        
        List<Object3D> objects = scene.getObjects();
        List<Light> lights = scene.getLights();
        
        int triangleCount = objects.stream()
            .mapToInt(obj -> obj instanceof Model3D ? ((Model3D) obj).getTriangles().size() : 0)
            .sum();
        
        Vector3D[][] rayPositions = mainCamera.calculatePositionsToRay();
        Vector3D cameraPos = mainCamera.getPosition();
        double[] depthRange = {
            cameraPos.getZ() + nearFarPlanes[0], 
            cameraPos.getZ() + nearFarPlanes[1]
        };
        
        int numThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        System.out.println("Using " + numThreads + " processing threads.");
        
        int tilesX = (width + TILE_SIZE - 1) / TILE_SIZE;
        int tilesY = (height + TILE_SIZE - 1) / TILE_SIZE;
        int totalTiles = tilesX * tilesY;
        
        System.out.println("Processing " + totalTiles + " tiles of " + TILE_SIZE + "x" + TILE_SIZE + " pixels");
        System.out.println("Total triangles: " + triangleCount);
        
        ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);
        AtomicInteger completedTiles = new AtomicInteger(0);
        
        ScheduledExecutorService progressExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ProgressReporter");
            t.setDaemon(true);
            return t;
        });
        
        ScheduledFuture<?> progressTask = progressExecutor.scheduleAtFixedRate(() -> {
            int completed = completedTiles.get();
            double progress = (double) completed / totalTiles * 100.0;
            long estimatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.printf("\rProgress: %.1f%% (%d/%d tiles) | Memory: %.1fMB", 
                progress, completed, totalTiles, estimatedMemory / (1024.0 * 1024.0));
        }, 0, PROGRESS_UPDATE_MS, TimeUnit.MILLISECONDS);

        try {
            CompletableFuture<Void>[] tasks = new CompletableFuture[totalTiles];
            int taskIndex = 0;
            
            for (int ty = 0; ty < tilesY; ty++) {
                for (int tx = 0; tx < tilesX; tx++) {
                    final int tileX = tx;
                    final int tileY = ty;
                    
                    tasks[taskIndex++] = CompletableFuture.runAsync(() -> {
                        renderTile(
                            tileX, tileY, TILE_SIZE, width, height,
                            rayPositions, cameraPos, depthRange,
                            objects, lights, scene, rgbBuffer, completedTiles
                        );
                    }, forkJoinPool);
                }
            }
            
            CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks);
            allTasks.get(100, TimeUnit.HOURS); // Timeout de 30 minutos
            
        } catch (TimeoutException e) {
            System.err.println("\nTimeout: The render took too long");
            return image;
        } catch (Exception e) {
            System.err.println("\nError during the render: " + e.getMessage());
            e.printStackTrace();
            return image;
        } finally {
            progressTask.cancel(false);
            progressExecutor.shutdown();
            forkJoinPool.shutdown();
            
            try {
                if (!forkJoinPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    forkJoinPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                forkJoinPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, rgbBuffer[y * width + x]);
            }
        }
        
        System.out.println("Render completed");
        return image;
    }
    
   
    private static void renderTile(
            int tileX, int tileY, int tileSize, int imageWidth, int imageHeight,
            Vector3D[][] rayPositions, Vector3D cameraPos, double[] depthRange,
            List<Object3D> objects, List<Light> lights, Scene scene, 
            int[] rgbBuffer, AtomicInteger completedTiles) {
        
        int startX = tileX * tileSize;
        int endX = Math.min(startX + tileSize, imageWidth);
        int startY = tileY * tileSize;
        int endY = Math.min(startY + tileSize, imageHeight);
        
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                Vector3D pixelPos = rayPositions[x][y];
                Vector3D worldPos = new Vector3D(
                    pixelPos.getX() + cameraPos.getX(),
                    pixelPos.getY() + cameraPos.getY(),
                    pixelPos.getZ() + cameraPos.getZ()
                );
                
                Ray ray = new Ray(cameraPos, worldPos);
                Intersection intersection = raycast(ray, objects, null, depthRange);
                
                Color pixelColor = Color.BLACK;
                if (intersection != null) {
                    pixelColor = calculatePixelColor(intersection, scene, lights, objects, ray, 0);
                }
                
                rgbBuffer[y * imageWidth + x] = pixelColor.getRGB();
            }
        }
        
        completedTiles.incrementAndGet();
    }
    
    private static Color calculatePixelColor(Intersection intersection, Scene scene, List<Light> lights, List<Object3D> objects, Ray ray, int depth) {
        Color pixelColor = Color.BLACK;
        Color objColor = intersection.getObject().getMaterial().getColor();
        
        for (Light light : lights) {
            Color diffuseColor = calculateLightContribution(light, intersection, objects, objColor);
            pixelColor = addColor(pixelColor, diffuseColor);
            
            Color specularColor = calculateSpecularLight(light, intersection, scene, objects);
            pixelColor = addColor(pixelColor, specularColor);
        }
        
        Color ambientContribution = calculateAmbientLight(objColor);
        pixelColor = addColor(pixelColor, ambientContribution);
        
        double reflectionFactor = intersection.getObject().getMaterial().getReflectivity();
        double refractionIndex = intersection.getObject().getMaterial().getRefractivity();
        double transparencyFactor = intersection.getObject().getMaterial().getTransparency();
        
        if (reflectionFactor > 0 && depth < scene.getMaxReflectionDepth()) {
            Color reflectedColor = calculateReflection(ray, intersection, scene, objects, depth + 1);
            pixelColor = blendColors(pixelColor, reflectedColor, reflectionFactor);
        }

        if (refractionIndex > 1.0 && transparencyFactor > 0 && depth < scene.getMaxReflectionDepth()) {
            Color refractedColor = calculateRefraction(ray, intersection, scene, objects, depth + 1);
            pixelColor = blendColors(pixelColor, refractedColor, transparencyFactor);
        }

        return pixelColor;
    }
    
    private static Color calculateReflection(Ray ray, Intersection intersection, Scene scene, List<Object3D> objects, int depth) {
        Vector3D normal = intersection.getNormal();
        Vector3D rayDirection = ray.getDirection();
        
        Vector3D I = Vector3D.normalize(rayDirection);
        
        double dotProduct = Vector3D.dotProduct(normal, I);
        Vector3D reflection = Vector3D.substract(I, Vector3D.scalarMultiplication(normal, 2 * dotProduct));
        reflection = Vector3D.normalize(reflection);
        
        Vector3D newOrigin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(reflection, EPSILON));
        Ray reflectedRay = new Ray(newOrigin, reflection);
        
        Intersection reflectedIntersection = raycast(reflectedRay, objects, intersection.getObject(), null);
        
        if (reflectedIntersection == null) {
            return Color.BLACK;
        }
        
        return calculatePixelColor(reflectedIntersection, scene, scene.getLights(), objects, reflectedRay, depth);
    }

    private static Color calculateRefraction(Ray ray, Intersection intersection, Scene scene, List<Object3D> objects, int depth) {
        if (depth >= scene.getMaxReflectionDepth()) {
            return Color.BLACK;
        }
        
        Vector3D normal = intersection.getNormal();
        Vector3D incomingDirection = Vector3D.normalize(ray.getDirection());
        Object3D currentObject = intersection.getObject();
        
        double n1 = 1.0;
        double n2 = currentObject.getMaterial().getRefractivity();
        
        double cosI = Vector3D.dotProduct(Vector3D.scalarMultiplication(incomingDirection, -1), normal);
        boolean entering = cosI > 0;
        
        if (!entering) {
            normal = Vector3D.scalarMultiplication(normal, -1);
            double temp = n1;
            n1 = n2;
            n2 = temp;
            cosI = -cosI;
        }
        
        double eta = n1 / n2;
        double k = 1.0 - eta * eta * (1.0 - cosI * cosI);
        
        if (k < 0.0) {
            return calculateReflection(ray, intersection, scene, objects, depth);
        }
        
        double cosThetaT = Math.sqrt(k);
        Vector3D refractedDirection = Vector3D.add(
            Vector3D.scalarMultiplication(incomingDirection, eta),
            Vector3D.scalarMultiplication(normal, eta * cosI - cosThetaT)
        );
        refractedDirection = Vector3D.normalize(refractedDirection);
        
        Vector3D offsetPoint = Vector3D.add(
            intersection.getPosition(), 
            Vector3D.scalarMultiplication(refractedDirection, EPSILON)
        );
        Ray refractedRay = new Ray(offsetPoint, refractedDirection);
        
        Intersection nextIntersection = raycast(refractedRay, objects, entering ? null : currentObject, null);
        
        if (nextIntersection == null) {
            return Color.BLACK;
        }
        
        if (entering && nextIntersection.getObject().equals(currentObject)) {
            double distanceInMaterial = nextIntersection.getDistance();
            Color materialColor = currentObject.getMaterial().getColor();
            double absorption = Math.exp(-distanceInMaterial * 0.1);
            
            Color exitColor = calculateRefraction(refractedRay, nextIntersection, scene, objects, depth + 1);
            
            return applyMaterialAbsorption(exitColor, materialColor, absorption);
        } else {
            return calculatePixelColor(nextIntersection, scene, scene.getLights(), objects, refractedRay, depth + 1);
        }
    }

    private static Color applyMaterialAbsorption(Color lightColor, Color materialColor, double absorption) {
        double[] lightRGB = colorToNormalizedArray(lightColor);
        double[] materialRGB = colorToNormalizedArray(materialColor);
        
        double[] resultRGB = new double[3];
        for (int i = 0; i < 3; i++) {
            resultRGB[i] = lightRGB[i] * Math.pow(materialRGB[i], 1.0 - absorption);
        }
        
        return createClampedColor(resultRGB);
    }

    private static Color blendColors(Color baseColor, Color overlayColor, double factor) {
        factor = Math.max(0.0, Math.min(1.0, factor));
        
        int r = (int) (baseColor.getRed() * (1 - factor) + overlayColor.getRed() * factor);
        int g = (int) (baseColor.getGreen() * (1 - factor) + overlayColor.getGreen() * factor);
        int b = (int) (baseColor.getBlue() * (1 - factor) + overlayColor.getBlue() * factor);
        
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        return new Color(r, g, b);
    }

    private static Color calculateLightContribution(Light light, Intersection intersection, List<Object3D> objects, Color objColor) {
        if (isPointInShadow(light, intersection, objects)) {
            return Color.BLACK;
        }
        
        double nDotL = light.getNDotL(intersection);
        
        if (nDotL <= 0) {
            return Color.BLACK;
        }
        
        double intensity = calculateLightIntensity(light, intersection, nDotL);
        
        if (intensity < 0.001) {
            return Color.BLACK;
        }
        
        return applyLightToColor(objColor, light.getColor(), intensity);
    }

    private static Color calculateSpecularLight(Light light, Intersection intersection, Scene scene, List<Object3D> objects) {
        if (isPointInShadow(light, intersection, objects)) {
            return Color.BLACK;
        }
        
        Vector3D N = intersection.getNormal();
        Vector3D L = Vector3D.normalize(Vector3D.substract(light.getPosition(), intersection.getPosition()));
        Vector3D V = Vector3D.normalize(Vector3D.substract(scene.getCamera().getPosition(), intersection.getPosition()));
        Vector3D R = Vector3D.normalize(Vector3D.substract(Vector3D.scalarMultiplication(N, 2 * Vector3D.dotProduct(N, L)), L));

        double k_s = intersection.getObject().getMaterial().getSpecular();
        double alpha = intersection.getObject().getMaterial().getShininess();
        
        if (k_s <= 0 || alpha <= 0) {
            return Color.BLACK;
        }

        double RdotV = Vector3D.dotProduct(R, V);
        if (RdotV <= 0) {
            return Color.BLACK;
        }
        
        double specIntensity = k_s * Math.pow(RdotV, alpha);
        specIntensity *= calculateLightIntensityForSpecular(light, intersection);
        
        double[] lightColors = colorToNormalizedArray(light.getColor());
        double[] specularColor = new double[]{
            specIntensity * lightColors[0],
            specIntensity * lightColors[1],
            specIntensity * lightColors[2]
        };

        return createClampedColor(specularColor);
    }

    private static double calculateLightIntensityForSpecular(Light light, Intersection intersection) {
        double intensity = light.getIntensity();
        
        if (light instanceof PointLight) {
            double distance = Vector3D.magnitude(Vector3D.substract(light.getPosition(), intersection.getPosition()));
            double falloff = 1.0 / (Math.pow(distance, 2));
            return intensity * falloff;
        } 
        else if (light instanceof DirectionalLight) {
            return intensity;
        }
        
        return intensity;
    }

    private static boolean isPointInShadow(Light light, Intersection intersection, List<Object3D> objects) {
        if (!light.getAbleShadow()) {
            return false;
        }
        
        Vector3D lightDirection = Vector3D.normalize(Vector3D.substract(light.getPosition(), intersection.getPosition()));
        Vector3D offsetPoint = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(lightDirection, EPSILON));
        Ray lightRay = new Ray(offsetPoint, lightDirection);
        
        double distanceObjectLight = Vector3D.magnitude(Vector3D.substract(light.getPosition(), intersection.getPosition()));
        
        Intersection shadowIntersection = shadowCast(lightRay, objects, null);
        
        return shadowIntersection != null && shadowIntersection.getDistance() <= distanceObjectLight;
    }

    private static double calculateLightIntensity(Light light, Intersection intersection, double nDotL) {
        if (nDotL <= 0) {
            return 0.0;
        }
        
        double intensity = light.getIntensity() * nDotL;
        
        if (light instanceof PointLight) {
            double distance = Vector3D.magnitude(Vector3D.substract(light.getPosition(), intersection.getPosition()));
            double falloff = 1.0 / (1 + Math.pow(distance, 2));
            return intensity * falloff;
        } 
        else if (light instanceof DirectionalLight) {
            return intensity;
        }
        
        return intensity;
    }

    private static Color applyLightToColor(Color objColor, Color lightColor, double intensity) {
        double[] lightColors = colorToNormalizedArray(lightColor);
        double[] objColors = colorToNormalizedArray(objColor);
        
        for (int i = 0; i < objColors.length; i++) {
            objColors[i] *= intensity * lightColors[i];
        }
        
        return createClampedColor(objColors);
    }

    private static Color calculateAmbientLight(Color objColor) {
        double[] objColors = {
            objColor.getRed() / 255.0,    
            objColor.getGreen() / 255.0,  
            objColor.getBlue() / 255.0    
        };
        
        double[] ambientColor = colorToNormalizedArray(Color.WHITE);
        double intensityAmbient = 0.01;
        
        for (int i = 0; i < objColors.length; i++) {
            objColors[i] *= intensityAmbient * ambientColor[i];
        }
        
        return createClampedColor(objColors);
    }

    private static double[] colorToNormalizedArray(Color color) {
        return new double[] {
            color.getRed() / 255.0,
            color.getGreen() / 255.0,
            color.getBlue() / 255.0
        };
    }

    private static Color createClampedColor(double[] colorArray) {
        return new Color(
            (float) Math.clamp(colorArray[0], 0.0, 1.0),
            (float) Math.clamp(colorArray[1], 0.0, 1.0),
            (float) Math.clamp(colorArray[2], 0.0, 1.0)
        );
    }
    
    public static Color addColor(Color original, Color otherColor) {
        float red = (float) Math.clamp((original.getRed() / 255.0) + (otherColor.getRed() / 255.0), 0.0, 1.0);
        float green = (float) Math.clamp((original.getGreen() / 255.0) + (otherColor.getGreen() / 255.0), 0.0, 1.0);
        float blue = (float) Math.clamp((original.getBlue() / 255.0) + (otherColor.getBlue() / 255.0), 0.0, 1.0);
        return new Color(red, green, blue);
    }

    public static Intersection raycast(Ray ray, List<Object3D> objects, Object3D caster, double[] clippingPlanes) {
        Intersection closestIntersection = null;

        for (int i = 0; i < objects.size(); i++) {
            Object3D currObj = objects.get(i);
            
            if (caster == null || !currObj.equals(caster)) {
                Intersection intersection = currObj.getIntersection(ray);
                
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    double intersectionZ = intersection.getPosition().getZ();

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

    public static Intersection shadowCast(Ray ray, List<Object3D> objects, Object3D caster) {
        Intersection closestIntersection = null;

        for (int i = 0; i < objects.size(); i++) {
            Object3D currObj = objects.get(i);
            
            if (caster == null || !currObj.equals(caster)) {
                Intersection intersection = currObj.getIntersection(ray);
                
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    
                    if (distance >= 0 && (closestIntersection == null || distance < closestIntersection.getDistance())) {
                        closestIntersection = intersection;        
                    }
                }
            }
        }
        return closestIntersection;
    }
}