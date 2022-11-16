package fr.game.core.maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class NoiseMap {
    public static float AMPLITUDE = 80f;
    public static int OCTAVES = 7;
    public static float ROUGHNESS = 0.3f;

    private Random random = new Random();
    private long seed;
    private int xOffset = 0;
    private int zOffset = 0;
    private int getHeight;

    public NoiseMap() {
        this.seed = random.nextInt(100000000);
    }

    //only works with POSITIVE gridX and gridZ values!
    public NoiseMap(int gridX, int gridZ, int vertexCount, long seed) {
        this.seed = seed;
        xOffset = gridX * (vertexCount - 1);
        zOffset = gridZ * (vertexCount - 1);
    }

    public float generateHeight(int x, int z) {

        x = x < 0 ? -x : x;
        z = z < 0 ? -z : z;

        float total = 0;
        float d = (float) Math.pow(2, OCTAVES - 1);
        for (int i = 0; i < OCTAVES; i++) {
            float freq = (float) (Math.pow(2, i) / d);
            float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp;
        }
        getHeight = (int) total;
        return (float) (int) total;
    }

    public int getHeight() {
        return getHeight;
    }

    public void setGetHeight(int getHeight) {
        this.getHeight = getHeight;
    }

    private float getInterpolatedNoise(float x, float z) {
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;

        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
    }

    private float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float) (1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private float getSmoothNoise(int x, int z) {
        float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1)) / 16f;
        float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1)
                + getNoise(x, z + 1)) / 8f;
        float center = getNoise(x, z) / 4f;
        return corners + sides + center;
    }

    private float getNoise(int x, int z) {
        random.setSeed(x * 49632 + z * 325176 + seed);
        return random.nextFloat() * 2f - 1f;
    }

    public void saveHeightmap(String name, int width, int height, int[][] heightmap) {
        int x;
        int y;

        // Save as bmp
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (x = 0; x < width; x++) {
            for (y = 0; y < height; y++) {
                int value = heightmap[x][y];
                // if value > 1, green
                if (value >= 2) {
                    // Set green

                    int baseGreen = 0x00FF00;
                    int mediumGreen = 0x00AA00;
                    int darkGreen = 0x005500;

                    if (value >= 5) {
                        if (value >= 20) {
                            image.setRGB(x, y, darkGreen);
                        } else {
                            image.setRGB(x, y, mediumGreen);
                        }
                    } else {
                        image.setRGB(x, y, baseGreen);
                    }
                }
                // if value > 0, yellow
                else if (value >= 0) {
                    image.setRGB(x, y, 0xFFFF00);
                }
                // if value > -1, blue
                else {
                    image.setRGB(x, y, 0x0000FF);
                }
            }
        }

        try {
            ImageIO.write(image, "bmp", new File(name + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save heightmap.data (seed, width, height)
        try {
            File file = new File(name + ".data");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(seed + " " + width + " " + height);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
