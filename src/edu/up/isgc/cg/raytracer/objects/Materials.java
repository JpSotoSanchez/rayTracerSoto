package edu.up.isgc.cg.raytracer.objects;

import java.awt.Color;

public class Materials {
    public static final Material MATTE_RED = new Material(Color.RED, 0.1, 5.0, 0.0, 1.0, 0.0);
    public static final Material MATTE_GREEN = new Material(Color.GREEN, 0.1, 5.0, 0.0, 1.0, 0.0);
    public static final Material MATTE_BLUE = new Material(Color.BLUE, 0.1, 5.0, 0.0, 1.0, 0.0);
    public static final Material MATTE_WHITE = new Material(Color.WHITE, 0.1, 5.0, 0.0, 1.0, 0.0);
    public static final Material MATTE_BLACK = new Material(Color.BLACK, 0.1, 5.0, 0.0, 1.0, 0.0);
    
    public static final Material GLOSSY_RED = new Material(Color.RED, 0.8, 32.0, 0.3, 1.0, 0.0);
    public static final Material GLOSSY_GREEN = new Material(Color.GREEN, 0.8, 32.0, 0.3, 1.0, 0.0);
    public static final Material GLOSSY_BLUE = new Material(Color.BLUE, 0.8, 32.0, 0.3, 1.0, 0.0);
    public static final Material GLOSSY_WHITE = new Material(Color.WHITE, 0.8, 32.0, 0.3, 1.0, 0.0);
    
    public static final Material MIRROR = new Material(Color.LIGHT_GRAY, 1.0, 100.0, 0.9, 1.0, 0.0);
    public static final Material CHROME = new Material(new Color(192, 192, 192), 0.9, 64.0, 0.7, 1.0, 0.0);
    public static final Material GOLD = new Material(new Color(255, 215, 0), 0.7, 25.0, 0.4, 1.0, 0.0);
    public static final Material COPPER = new Material(new Color(184, 115, 51), 0.6, 20.0, 0.3, 1.0, 0.0);
    public static final Material STEEL = new Material(new Color(192, 192, 192), 0.9, 80.0, 0.7, 1.0, 0.0);
    public static final Material ALUMINUM = new Material(new Color(200, 200, 200), 0.8, 60.0, 0.6, 1.0, 0.0);
    public static final Material IRON = new Material(new Color(128, 128, 128), 0.7, 40.0, 0.5, 1.0, 0.0);
    public static final Material SILVER = new Material(new Color(220, 220, 220), 1.0, 100.0, 0.9, 1.0, 0.0);
    public static final Material BRONZE = new Material(new Color(205, 127, 50), 0.6, 40.0, 0.5, 1.0, 0.0);
    public static final Material TITANIUM = new Material(new Color(105, 105, 105), 0.85, 70.0, 0.15, 1.0, 0.0);

    public static final Material STONE_GRANITE = new Material(new Color(112, 128, 144), 0.2, 10.0, 0.05, 1.0, 0.0);
    public static final Material STONE_MARBLE = new Material(new Color(220, 220, 220), 0.3, 20.0, 0.1, 1.0, 0.0);
    public static final Material STONE_SLATE = new Material(new Color(47, 79, 79), 0.1, 5.0, 0.05, 1.0, 0.0);
    public static final Material STONE_LIMESTONE = new Material(new Color(200, 200, 170), 0.1, 5.0, 0.05, 1.0, 0.0);
    public static final Material STONE_BASALT = new Material(new Color(60, 60, 60), 0.15, 8.0, 0.05, 1.0, 0.0);
    public static final Material STONE_OBSIDIAN = new Material(new Color(20, 20, 30), 0.5, 50.0, 0.2, 1.0, 0.0);
    public static final Material STONE_SANDSTONE = new Material(new Color(194, 178, 128), 0.1, 4.0, 0.05, 1.0, 0.0);
    public static final Material STONE_CONCRETE = new Material(new Color(130, 130, 130), 0.2, 10.0, 0.05, 1.0, 0.0);

    public static final Material GLASS = new Material(new Color(255, 255, 255, 50), 0.9, 100.0, 0.0, 1.5, 0.9);
    public static final Material DIAMOND = new Material(new Color(245, 245, 245), 1.0, 300.0, 0.65, 2.417, 0.95);
    public static final Material CRYSTAL_RED = new Material(new Color(255, 80, 80, 100), 0.5, 100.0, 0.1, 1.54, 0.55);
    public static final Material CRYSTAL_GREEN = new Material(new Color(80, 255, 80, 100), 0.5, 100.0, 0.1, 1.54, 0.55);
    public static final Material CRYSTAL_BLUE = new Material(new Color(80, 80, 255, 100), 0.5, 100.0, 0.1, 1.54, 0.55);
    public static final Material CRYSTAL_PURPLE = new Material(new Color(180, 80, 180, 100), 0.5, 100.0, 0.1, 1.54, 0.55);
    public static final Material CRYSTAL_YELLOW = new Material(new Color(255, 230, 80, 100), 0.5, 100.0, 0.1, 1.54, 0.5);
    public static final Material WATER = new Material(new Color(173, 216, 230, 100), 0.8, 50.0, 0.2, 1.33, 0.8);
    public static final Material PLASTIC_RED = new Material(Color.RED, 0.5, 15.0, 0.1, 1.0, 0.0);
    public static final Material PLASTIC_GREEN = new Material(Color.GREEN, 0.5, 15.0, 0.1, 1.0, 0.0);
    public static final Material PLASTIC_BLUE = new Material(Color.BLUE, 0.5, 15.0, 0.1, 1.0, 0.0);
    
    public static final Material RUBBER_BLACK = new Material(Color.BLACK, 0.1, 2.0, 0.0, 1.0, 0.0);
    
    public static final Material WOOD_BROWN = new Material(new Color(139, 69, 19), 0.2, 5.0, 0.0, 1.0, 0.0);
    public static final Material WOOD_DARK_RICH = new Material(new Color(45, 25, 10), 0.15, 8.0, 0.05, 1.0, 0.0);
    public static final Material WOOD_DARK_POLISHED = new Material(new Color(50, 30, 15), 0.25, 20.0, 0.1, 1.0, 0.0);
    public static final Material WOOD_DARK_OLD = new Material(new Color(40, 20, 10), 0.1, 4.0, 0.01, 1.0, 0.0);

    public static final Material STRAW = new Material(new Color(210, 180, 140), 0.05, 2.0, 0.0, 1.0, 0.0);
    public static final Material BRICK = new Material(new Color(178, 34, 34), 0.4, 15.0, 0.2, 1.0, 0.0);
}
