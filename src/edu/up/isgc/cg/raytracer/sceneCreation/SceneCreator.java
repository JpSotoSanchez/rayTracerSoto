package edu.up.isgc.cg.raytracer.sceneCreation;

import java.awt.Color;
import java.util.Random;

import edu.up.isgc.cg.raytracer.Scene;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.lights.DirectionalLight;
import edu.up.isgc.cg.raytracer.lights.PointLight;
import edu.up.isgc.cg.raytracer.objects.Camera;
import edu.up.isgc.cg.raytracer.objects.Sphere;
import edu.up.isgc.cg.raytracer.tools.OBJReader;
import edu.up.isgc.cg.raytracer.objects.Material;
import edu.up.isgc.cg.raytracer.objects.Materials;
import edu.up.isgc.cg.raytracer.objects.Plane;


public class SceneCreator {
    public static Scene createScene01() {
        Scene scene = new Scene();
        scene.setCamera(new Camera(
            new Vector3D(0, 0, -4),
            60,
            60,
            800,
            800,
            0.6,
            50.0
        ));
        scene.setMaxReflectionDepth(1);

        scene.addLight(new DirectionalLight(
            new Vector3D(-1.0, 0.0, 0.0),
            Color.WHITE,
            8,
            false
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.0, -2, -5),
            Color.blue,
            1,
            true
        ));

        scene.addLight(new PointLight(
            new Vector3D(-10, 0, 3),
            Color.WHITE,
            1,
            true
        ));

        scene.addObject(new Sphere(
            new Vector3D(0.5, 1, 8),
            0.8,
            Materials.MATTE_RED
        ));

        scene.addObject(new Sphere(
            new Vector3D(0.1, 1, 6),
            0.5,
            Materials.MATTE_BLUE
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/SmallTeapot.obj",
            new Vector3D(-5, 0, 3),
            Materials.MATTE_WHITE,
            0.75,
            0.75,
            0.75,
            0,
            0,
            0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/SmallTeapot.obj",
            new Vector3D(0, -1, 3),
            Materials.GLOSSY_RED,
            2,
            2,
            2,
            0,
            0,
            0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Ring.obj",
            new Vector3D(7, 0, 3),
            Materials.GLOSSY_WHITE,
            2,
            2,
            2,
            90,
            0,
            0
        ));

        return scene;
    }

    public static Scene createScene02() {
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(1);
        scene.setCamera(new Camera(
            new Vector3D(0, 0, -4),
            106.7,
            50,
            3840,
            2160,
            0.6,
            50.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.0, 1.0, 0.0),
            Color.WHITE,
            5,
            true
        ));

        // Esfera roja mate grande
        scene.addObject(new Sphere(
            new Vector3D(0.0, 1.0, 5.0),
            0.5,
            Materials.MATTE_RED
        ));

        // Esfera amarilla con plástico
        scene.addObject(new Sphere(
            new Vector3D(0.5, 1.0, 4.5),
            0.25,
            new Material(new Color(200, 255, 0), 0.5, 15.0, 0.1, 1.0, 0.0) // Similar a plastic
        ));

        // Esfera azul mate
        scene.addObject(new Sphere(
            new Vector3D(0.35, 1.0, 4.5),
            0.3,
            Materials.MATTE_BLUE
        ));

        // Esfera rosa brillante (especular alta)
        scene.addObject(new Sphere(
            new Vector3D(4.85, 1.0, 4.5),
            0.3,
            new Material(Color.PINK, 0.9, 64, 0.0, 1.0, 0.0) // Rosa brillante
        ));

        // Esfera azul lejana
        scene.addObject(new Sphere(
            new Vector3D(2.85, 1.0, 304.5),
            0.5,
            Materials.MATTE_BLUE
        ));

        // Cubo cromado reflectante
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Cube.obj",
            new Vector3D(0f, -2.5, 1.0),
            Materials.CHROME,
            1,
            1,
            1,
            0,
            45,
            0
        ));

        // Cubo quad verde reflectante
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/CubeQuad.obj",
            new Vector3D(-3.0, -2.5, 3.0),
            new Material(Color.GREEN, 1.0, 1.0, 0.2, 1.0, 0.0), // Verde con reflexión personalizada
            1,
            1,
            1,
            0,
            0,
            0
        ));

        // Tetera roja brillante especular
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/SmallTeapot.obj",
            new Vector3D(2.0, -1.0, 1.5),
            new Material(Color.RED, 0.9, 100, 0.5, 1.0, 0.0), // Roja muy brillante
            1,
            1,
            1,
            0,
            90,
            0
        ));

        // Tetera azul menos brillante
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/SmallTeapot.obj",
            new Vector3D(-1, -1.0, 1.5),
            new Material(Color.BLUE, 0.3, 34, 0.5, 1.0, 0.0), // Azul moderadamente brillante
            1,
            1,
            1,
            0,
            90,
            0
        ));

        // Anillo azul con poca reflexión
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Ring.obj",
            new Vector3D(2.0, -1.0, 1.5),
            new Material(Color.BLUE, 0.3, 0.2, 0.0, 1.0, 0.0), // Azul mate
            1,
            1,
            1,
            0,
            0,
            0
        ));

        return scene;
    }

    public static Scene createScene03() {
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(3);
        scene.setCamera(new Camera(new Vector3D(0, 0, -4), 60, 60, 800, 800, 0.6, 50.0));
        scene.addLight(new PointLight(new Vector3D(0.0, 5, -5), Color.WHITE, 1, true));
        scene.addLight(new DirectionalLight(new Vector3D(0.0, -1, 5), Color.WHITE, 1, false));
        scene.addLight(new PointLight(new Vector3D(-1, 0, 0), Color.WHITE, 15, true));

        // Fondo verde grande
        scene.addObject(OBJReader.getModel3D("src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\CubeQuad.obj",
            new Vector3D(0, 0, 30),
            Materials.MATTE_GREEN,
            10, 10, 10,
            0, 0, 0));

        // Cubo rojo del suelo
        scene.addObject(OBJReader.getModel3D("src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\CubeQuad.obj",
            new Vector3D(0, -11, 1),
            Materials.MATTE_RED,
            5, 5, 5,
            0, 0, 0));

        // Tetera de vidrio transparente (solo refracción)
         scene.addObject(OBJReader.getModel3D("src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\SmallTeapot.obj",
             new Vector3D(0, -2, -1),
             Materials.GLASS,
             1.5, 1.5, 1.5,
             0, 0, 0));

        // Esfera blanca mate
        scene.addObject(new Sphere(new Vector3D(0, 2, -6), 1, Materials.MATTE_WHITE));

        // Esfera de cristal transparente
        scene.addObject(new Sphere(
            new Vector3D(2, 3, 5),
            1.0,
            Materials.GLASS
        ));

        // Esfera de agua
         scene.addObject(new Sphere(
             new Vector3D(-2, 3, 5),
             1.0,
             Materials.WATER
         ));

        return scene;
    }

    public static Scene createScene04() {
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(5);
        scene.setCamera(new Camera(new Vector3D(0, 0, -3), 60, 60, 800, 800, 0.6, 50.0));
        scene.addLight(new DirectionalLight(new Vector3D(-0.5, -0.5, 1), Color.WHITE, 1.0, false));

        // Fondo verde
        scene.addObject(OBJReader.getModel3D("src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\CubeQuad.obj",
            new Vector3D(0, 0, 25),
            Materials.MATTE_GREEN,
            15, 15, 15,
            0, 0, 0));

        // Objeto rojo brillante para ver a través de la refracción
        scene.addObject(OBJReader.getModel3D("src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\CubeQuad.obj",
            new Vector3D(-2, -2, 8),
            Materials.GLOSSY_RED,
            3, 3, 3,
            0, 0, 0));

        // Tetera azul plástica
        scene.addObject(OBJReader.getModel3D("src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\SmallTeapot.obj",
            new Vector3D(2, -1, 6),
            Materials.PLASTIC_BLUE,
            1.5, 1.5, 1.5,
            0, 0, 0));

        // Esfera de vidrio transparente (PRINCIPAL)
        scene.addObject(new Sphere(
            new Vector3D(0, 0, 1),
            1.2,
            Materials.GLASS
        ));

        return scene;
    }

    public static Scene createScene05() {
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(2);

        scene.setCamera(new Camera(new Vector3D(0, 0, -10), 106.7, 50, 4096, 2160, 0.6, 500.0));


        scene.addLight(new DirectionalLight(
            new Vector3D(0, -1, 1),
            Color.WHITE,
            1.0,
            false
        ));

        // Materiales variados para las teteras
        Material[] teapotMaterials = {
            Materials.MATTE_RED, Materials.GOLD, new Material(Color.YELLOW, 0.1, 5.0, 0.0, 1.0, 0.0),
            Materials.MATTE_GREEN, new Material(Color.CYAN, 0.1, 5.0, 0.0, 1.0, 0.0), Materials.MATTE_BLUE,
            new Material(Color.MAGENTA, 0.1, 5.0, 0.0, 1.0, 0.0), Materials.COPPER, Materials.PLASTIC_RED
        };

        int rows = 3, cols = 3;
        double spacing = 3;
        double startX = -spacing * (cols-1) / 2;
        double startY = -spacing * (rows-1) / 2;
        double teapotZ = 0;

        // Crear las teteras con materiales variados
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = startX + j * spacing;
                double y = startY + i * spacing;
                scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\Crab.obj",
                    new Vector3D(x, y, teapotZ),
                    teapotMaterials[idx++],
                    1.2, 1.2, 1.2,
                    0, 0, 0
                ));
            }
        }

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/VikingShield.obj",
            new Vector3D(0, 0, -1),
            Materials.DIAMOND,
            1, 1, 1,
            0, 0, 0
        ));

        return scene;
    }

    public static Scene finalScene01(){
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(10);
        scene.setCamera(new Camera(new Vector3D(0, 3, -5), 106.7, 50, 4096, 2160, 0.6, 200.0));

        scene.addLight(new PointLight(new Vector3D(0, 15, 15), Color.WHITE, 15, false));
        scene.addLight(new PointLight(new Vector3D(60, 50, 45), Color.LIGHT_GRAY, 100, false));


        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\Ocean.obj",
                    new Vector3D(0, 0, 1),
                    Materials.WATER,
                    1, 1, 1,
                    0, 0, 0
        ));
        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\VikingLongship.obj",
                    new Vector3D(-10, 0, 5),
                    Materials.WOOD_DARK_OLD,
                    1, 1, 1,
                    5, 130, 0
        ));

        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\volcan.obj",
                    new Vector3D(50, -1, 50),
                    Materials.STONE_CONCRETE,
                    1, 1, 1,
                    0, 0, 0
        ));
        
        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\VikingAxe.obj",
                    new Vector3D(2, -0.2, -4),
                    Materials.IRON,
                    2, 2, 2,
                    30, -55, 0
        ));

        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\VikingShield.obj",
                    new Vector3D(-3, 1, -1),
                    Materials.STEEL,
                    5, 5, 5,
                    -15, -65, 0
        ));

        Random rand = new Random(8042005);
        for(int i = 0; i < 1500; i++){
            double x = rand.nextDouble() * 300 - 100;
            double y = rand.nextDouble() * 100 + 5;
            double z = rand.nextDouble() * 90 + 90;
            scene.addObject(new Sphere(new Vector3D(x, y, z), 0.2, Materials.MATTE_WHITE));
        }
        

        return scene;
    }

    public static Scene finalScene02(){
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(10);

        scene.setCamera(new Camera(new Vector3D(0, 0, -10), 120.0, 67.5, 4096, 2160, 0.6, 500.0));

        scene.addLight(new DirectionalLight(new Vector3D(0, 0, 1), new Color(180, 200, 220), 1.5, false));

        scene.addObject(new Plane(new Vector3D(0, 0, 170), new Vector3D(0, 0, 1), Materials.RUBBER_BLACK));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(-180, 85, 88),
            Materials.CRYSTAL_RED,
            0.28, 0.28, 0.28,
            -85, 0, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(-120, 77, 72),
            Materials.GOLD,
            0.32, 0.32, 0.32,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(-60, 82, 58),
            Materials.CHROME,
            0.1, 0.1, 0.1,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(0, 75, 25),
            Materials.CRYSTAL_BLUE,
            0.075, 0.075, 0.075,
            -85, 90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(60, 88, 58),
            Materials.COPPER,
            0.28, 0.28, 0.28,
            -85, 0, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(120, 81, 72),
            Materials.BRONZE,
            0.32, 0.32, 0.32,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(180, 84, 88),
            Materials.SILVER,
            0.1, 0.1, 0.1,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(-180, 45, 88),
            Materials.STONE_MARBLE,
            0.075, 0.075, 0.075,
            -85, -90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(-120, 38, 72),
            Materials.CRYSTAL_GREEN,
            0.32, 0.32, 0.32,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(-60, 42, 58),
            Materials.STEEL,
            0.28, 0.28, 0.28,
            -85, 180, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(0, 36, 25),
            Materials.CRYSTAL_PURPLE,
            0.1, 0.1, 0.1,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(60, 43, 58),
            Materials.TITANIUM,
            0.32, 0.32, 0.32,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(120, 39, 72),
            Materials.CRYSTAL_YELLOW,
            0.075, 0.075, 0.075,
            -85, -90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(180, 41, 88),
            Materials.ALUMINUM,
            0.28, 0.28, 0.28,
            -85, 180, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(-180, -3, 88),
            Materials.WOOD_BROWN,
            0.32, 0.32, 0.32,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(-120, 5, 72),
            Materials.STONE_OBSIDIAN,
            0.1, 0.1, 0.1,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(-60, 2, 58),
            Materials.MATTE_RED,
            0.28, 0.28, 0.28,
            -85, 0, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(0, 3, 2),
            Materials.IRON,
            0.1, 0.1, 0.1,
            0, 180, -90
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(60, 6, 58),
            Materials.STONE_GRANITE,
            0.32, 0.32, 0.32,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(120, 1, 72),
            Materials.GLOSSY_GREEN,
            0.1, 0.1, 0.1,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(180, 3, 88),
            Materials.STONE_SLATE,
            0.28, 0.28, 0.28,
            -85, 0, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(-180, -37, 88),
            Materials.GLOSSY_WHITE,
            0.1, 0.1, 0.1,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(-120, -44, 72),
            Materials.STONE_SANDSTONE,
            0.075, 0.075, 0.075,
            -85, -90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(-60, -41, 58),
            Materials.STONE_LIMESTONE,
            0.32, 0.32, 0.32,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(0, -38, 25),
            Materials.STONE_BASALT,
            0.28, 0.28, 0.28,
            -85, 180, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(60, -45, 58),
            Materials.STONE_MARBLE,
            0.075, 0.075, 0.075,
            -85, -90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(120, -42, 72),
            Materials.CRYSTAL_RED,
            0.1, 0.1, 0.1,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(180, -39, 88),
            Materials.GOLD,
            0.32, 0.32, 0.32,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(-180, -77, 88),
            Materials.CHROME,
            0.075, 0.075, 0.075,
            -85, 90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(-120, -83, 72),
            Materials.CRYSTAL_BLUE,
            0.28, 0.28, 0.28,
            -85, 0, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(-60, -79, 58),
            Materials.COPPER,
            0.1, 0.1, 0.1,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(0, -82, 25),
            Materials.BRONZE,
            0.32, 0.32, 0.32,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(60, -76, 58),
            Materials.SILVER,
            0.075, 0.075, 0.075,
            -85, 90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(120, -81, 72),
            Materials.CRYSTAL_GREEN,
            0.28, 0.28, 0.28,
            -85, 0, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(180, -78, 88),
            Materials.STEEL,
            0.32, 0.32, 0.32,
            -85, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(-180, -117, 88),
            Materials.CRYSTAL_PURPLE,
            0.1, 0.1, 0.1,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(-120, -123, 72),
            Materials.CRYSTAL_YELLOW,
            0.32, 0.32, 0.32,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(-60, -119, 58),
            Materials.TITANIUM,
            0.28, 0.28, 0.28,
            -85, 180, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(0, -122, 25),
            Materials.ALUMINUM,
            0.075, 0.075, 0.075,
            -85, -90, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(60, -116, 58),
            Materials.WOOD_BROWN,
            0.1, 0.1, 0.1,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(120, -121, 72),
            Materials.STONE_OBSIDIAN,
            0.32, 0.32, 0.32,
            -85, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(180, -118, 88),
            Materials.MATTE_RED,
            0.28, 0.28, 0.28,
            -85, 180, 10
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(-180, 125, 88),
            Materials.GLOSSY_BLUE,
            0.32, 0.32, 0.32,
            -85, 0, 90
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(-120, 118, 72),
            Materials.GLOSSY_GREEN,
            0.075, 0.075, 0.075,
            -85, 90, 90
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(-60, 122, 58),
            Materials.GLOSSY_WHITE,
            0.1, 0.1, 0.1,
            -85, 0, 90
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/HammerShark.obj",
            new Vector3D(0, 115, 25),
            Materials.STONE_GRANITE,
            0.28, 0.28, 0.28,
            -85, 0, 100
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/BaskingShark.obj",
            new Vector3D(60, 126, 58),
            Materials.STONE_SLATE,
            0.075, 0.075, 0.075,
            -85, 0, 90
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Shark.obj",
            new Vector3D(120, 121, 72),
            Materials.STONE_SANDSTONE,
            0.32, 0.32, 0.32,
            -85, 0, 90
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/WhaleShark.obj",
            new Vector3D(180, 119, 88),
            Materials.STONE_LIMESTONE,
            0.1, 0.1, 0.1,
            -85, 0, 90
        ));

        return scene;
    }

    public static Scene finalScene03(){
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(10);
        scene.setCamera(new Camera(new Vector3D(0, 5, -10), 106.7, 10, 4096, 2160, 0.6, 200.0));
        scene.addLight(new DirectionalLight(new Vector3D(0, 0, 1), Color.white, 1, false));
        scene.addLight(new DirectionalLight(new Vector3D(0, 1, 0), Color.white, 0.5, false));
        scene.addLight(new PointLight(new Vector3D(0, 50, 25), Color.ORANGE, 15, false));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/VikingShield.obj",
            new Vector3D(9, -4, -2),
            Materials.CRYSTAL_YELLOW,
            6, 6, 6,
            -35, 220, 0
        ));
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/VikingSword.obj",
            new Vector3D(-10, 50, 9),
            Materials.COPPER,
            25, 25, 25,
            0, 90, 115
        ));
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/VikingSword.obj",
            new Vector3D(10, 49, 8.5),
            Materials.CHROME,
            25, 25, 25,
            0, 90, -115
        ));
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/LongTable.obj",
            new Vector3D(0, -15, 6),
            Materials.WOOD_BROWN,
            15, 45, 15,
            180, 0, 180
        ));
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hourglass.obj",
            new Vector3D(-7, 1, 6),
            Materials.CRYSTAL_BLUE,
            0.25, 0.25, 0.25,
            -90, 20, 0
        ));
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Barrel.obj",
            new Vector3D(13, 3, 1),
            Materials.WOOD_BROWN,
            6, 15, 6,
            -180, 0, 0
        ));
         
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Guard.obj",
            new Vector3D(0, -25, 15),
            Materials.STONE_LIMESTONE,
            0.2, 0.3, 0.2,
            0, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Glass.obj",
            new Vector3D(-1, 1, 6),
            Materials.CRYSTAL_PURPLE,
            0.02, 0.02, 0.02,
            0, 0, 0
        ));
        
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/SkullShotGlass.obj",
            new Vector3D(5.5, 1, 6),
            Materials.STEEL,
            0.75, 0.75, 0.75,
            -90, 180, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/brick_wal.obj",
            new Vector3D(0, -5, 20),
            Materials.BRICK,
            5, 5, 5,
            0, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/brick_wal.obj",
            new Vector3D(0, -25, 0),
            Materials.STONE_SLATE,
            20, 5, 5,
            90, 0, 0
        ));


        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/torch.obj",
            new Vector3D(40, 10, 8),
            Materials.RUBBER_BLACK,
            2, 2, 2,
            0, 180, 0
        ));
        scene.addLight(new PointLight(new Vector3D(40, 15, 6), Color.RED, 5, true));

        
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/torch.obj",
            new Vector3D(-40, 10, 8),
            Materials.RUBBER_BLACK,
            2, 2, 2,
            0, 180, 0
        ));
        scene.addLight(new PointLight(new Vector3D(-40, 15, 6), Color.RED, 5, true));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/GrindStone.obj",
            new Vector3D(-25, -20, 6),
            Materials.STONE_OBSIDIAN,
            0.1, 0.2, 0.1,
            0, 90, 0
        ));        

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(-20, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(-40, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(-60, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(0, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));
        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(20, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(40, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Hay.obj",
            new Vector3D(60, 65, 10),
            Materials.STRAW,
            0.1, 0.1, 0.1,
            10, 0, 0
        ));


        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/entrance.obj",
            new Vector3D(0, -20, 10),
            Materials.STONE_BASALT,
            10, 15, 1,
            0, 0, 0
        ));

        scene.addObject(new Plane(new Vector3D(0, 0, 30), new Vector3D(0,0,1), Materials.CRYSTAL_BLUE));
        return scene;
    }

    public static Scene reflectionDisplay() {
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(3);
        scene.setCamera(new Camera(new Vector3D(0, 5, -10), 100, 50, 4096, 2160, 0.6, 200.0));

        scene.addObject(new Plane(new Vector3D(0, -50, 50), new Vector3D(0, 3, -1), Materials.MIRROR));


        scene.addLight(new DirectionalLight(new Vector3D(0, 0, 1), Color.white, 5, false));


        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\Crab.obj",
                    new Vector3D(-15, 10, 50),
                    Materials.GOLD,
                    2.5, 2.5, 2.5,
                    84,142, 43
        ));

        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\Palm.obj",
                    new Vector3D(200, 50, 80),
                    Materials.WOOD_BROWN,
                    1, 1, 1,
                    -70,0, 0
        ));

        scene.addObject(OBJReader.getModel3D(
                    "src\\edu\\up\\isgc\\cg\\raytracer\\objectsRender\\VikingAxe.obj",
                    new Vector3D(20, 50, 30),
                    Materials.GLOSSY_BLUE,
                    100, 100, 100,
                    0,10, 90
        ));


        return scene;
    }

    public static Scene refractionDisplay() {
        Scene scene = new Scene();
        scene.setMaxReflectionDepth(6); 
        scene.setCamera(new Camera(
            new Vector3D(0, 0, -10),     
            85.0,                        
            48.0,                        
            4096,                        
            2160,
            0.5,                         
            200.0                        
        ));

        
        scene.addLight(new DirectionalLight(
            new Vector3D(-0.3, -0.8, 0.5),  
            new Color(120, 180, 255),       
            3.0,                            
            false
        ));

        scene.addLight(new PointLight(
            new Vector3D(0, 25, 10),
            new Color(200, 230, 255),       
            12.0,                           
            true
        ));

        scene.addLight(new PointLight(
            new Vector3D(-20, 5, 5),
            new Color(100, 150, 220),       
            6.0,
            true
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/CubeQuad.obj",
            new Vector3D(0, 15, 0),         
            new Material(
                new Color(40, 100, 160, 180), 
                0.9,                          
                120,                          
                0.1,                          
                1.33,                         
                0.85                          
            ),
            100, 1, 100,                    
            0, 0, 0
        ));


        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Ring.obj",
            new Vector3D(-6, 1, 15),
            Materials.STONE_OBSIDIAN,
            1.8, 1.8, 1.8,
            60, 0, 30                       
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Ring.obj",
            new Vector3D(-4, 0, 16),
            Materials.ALUMINUM,
            1.5, 1.5, 1.5,
            30, 45, 0
        ));

        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/Crab.obj",
            new Vector3D(0, -1, 25),
            Materials.BRONZE,
            1.0, 1.0, 1.0,
            0, 180, 0
        ));

        scene.addObject(new Sphere(new Vector3D(0, 0, 1), 2.5, Materials.DIAMOND));


        scene.addObject(OBJReader.getModel3D(
            "src/edu/up/isgc/cg/raytracer/objectsRender/CubeQuad.obj",
            new Vector3D(0, -15, 50),       // Fondo lejano
            new Material(
                new Color(60, 80, 120),     // Azul oscuro del fondo marino
                0.1,
                10,
                0.05,
                1.0,                        // Sin refracción
                0.0                         // Opaco
            ),
            200, 30, 10,                    // Superficie amplia y baja
            0, 0, 0
        ));

        return scene;
    }

}