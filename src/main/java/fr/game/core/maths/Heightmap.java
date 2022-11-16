package fr.game.core.maths;

import fr.game.core.entity.terrain.ChunkCoordinates;
import fr.game.core.entity.terrain.World;
import org.joml.Vector2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Heightmap {

    public int CHUNK_HEIGHT = 10;
    public int CHUNK_WIDTH = 16;
    private int WATER_LEVEL = CHUNK_HEIGHT / 2;
    private int SAND_LEVEL = 3;
    private int BLOCK_SIZE = 1;
    private int MAP_SIZE;
    private float roughness = 0.032f;

    int heightmap[][];

    long seed = 0;

    NoiseMap noiseMap;

    public Heightmap(int mapSize, long seed) {
        this.seed = seed;
        MAP_SIZE = mapSize;
        this.heightmap = new int[MAP_SIZE][MAP_SIZE];
        noiseMap = new NoiseMap(0, mapSize, 1000, seed);
    }

    public void genHeightmap() {
        int i;
        int j;
        for (i = 0; i < MAP_SIZE; i++) {
            for (j = 0; j < MAP_SIZE; j++) {
                heightmap[i][j] = 0;
            }
        }

        for (i = 0; i < MAP_SIZE; i++) {
            for (j = 0; j < MAP_SIZE; j++) {
                heightmap[i][j] = (int) noiseMap.generateHeight(i, j);
            }
        }
    }

    public List<ChunkCoordinates> splitHeightmap(int size) {
        // Split heightmap into chunks (square of size size)
        List<ChunkCoordinates> chunksCoords = new ArrayList<>();
        for (int i = 0; i < MAP_SIZE; i += size) {
            for (int j = 0; j < MAP_SIZE; j += size) {
                // from (i, j) to (MAP_SIZE, MAP_SIZE  )
                Vector2f bottomLeft = new Vector2f(i, j);
                Vector2f bottomRight = new Vector2f(i + size, j);
                Vector2f topLeft = new Vector2f(i, j + size);
                Vector2f topRight = new Vector2f(i + size, j + size);

                chunksCoords.add(new ChunkCoordinates(bottomLeft, bottomRight, topLeft, topRight));
            }
        }

        // Draw an bmp image of the chunks coordinates (debug)
        return chunksCoords;
    }

    public void genHeightmap(float roughness, String name) {
        this.roughness = roughness;
        genHeightmap();
        saveHeightmap(name);
    }

    /**
     * Save heightmap to file (BMP)
     * <p>
     * Uint32 *pixels = calloc(CHUNK_WIDTH * CHUNK_WIDTH, 4);
     * int x;
     * int y;
     * for (y = 0; y < CHUNK_WIDTH; y++) {
     * for (x = 0; x < CHUNK_WIDTH; x++) {
     * Uint32 *bufp = pixels + (y * CHUNK_WIDTH) + x;
     * Uint32 color;
     * int height = heightmap[x][y];
     * if (height <= WATER_LEVEL)
     * color = SDL_MapRGB(format, 0, 0, height % 255);
     * else if (height <= WATER_LEVEL + SAND_LEVEL)
     * color = SDL_MapRGB(format, 255, 201, 175);
     * else
     * color = SDL_MapRGB(format, 0, height % 256, 0);
     * *bufp = color;
     * }
     */
    public void saveHeightmap(String name) {
        noiseMap.saveHeightmap(name, MAP_SIZE, MAP_SIZE, heightmap);
    }

    public void stepSquare(int x0, int y0, int x1, int y1) {
        // Finish recursion if needed.
        if (((x1 - x0) <= 1) || (y1 - y0) <= 1)
            return;

        // Middle coords.
        int mid_x = (x0 + x1) / 2;
        int mid_y = (y0 + y1) / 2;

        // Heights at corners.
        int top_left_height = heightmap[x0][y0];
        int top_right_height = heightmap[x1][y0];
        int bot_left_height = heightmap[x0][y1];
        int bot_right_height = heightmap[x1][y1];

        // Left
        heightmap[x0][mid_y] = average(top_left_height, bot_left_height);

        // Top
        heightmap[mid_x][y0] = average(top_left_height, top_right_height);

        // Right
        heightmap[x1][mid_y] = average(top_right_height, bot_right_height);

        // Bottom
        heightmap[mid_x][y1] = average(bot_left_height, bot_right_height);

        // Center point average
        heightmap[mid_x][mid_y] = (top_left_height + top_right_height + bot_left_height + bot_right_height) / 4;

        // Center point displace
        int length = x1 - x0;
        heightmap[mid_x][mid_y] += randomize((int) (-1 * roughness * length), (int) (roughness * length));

        // Repeat for smaller squares
        stepSquare(x0, y0, mid_x, mid_y);
        stepSquare(mid_x, y0, x1, mid_y);
        stepSquare(mid_x, mid_y, x1, y1);
        stepSquare(x0, mid_y, mid_x, y1);
    }

    public void genWater() {
        int i;
        int j;
        for (i = 0; i < MAP_SIZE; i++) {
            for (j = 0; j < MAP_SIZE; j++) {
                if (heightmap[i][j] < WATER_LEVEL)
                    heightmap[i][j] = WATER_LEVEL;
            }
        }
    }

    private void normalize() {
        int i;
        int j;
        for (i = 0; i < MAP_SIZE; i++) {
            for (j = 0; j < MAP_SIZE; j++) {
                if (heightmap[i][j] >= CHUNK_HEIGHT)
                    heightmap[i][j] = CHUNK_HEIGHT;
                if (heightmap[i][j] < 0)
                    heightmap[i][j] = 0;
            }
        }
    }

    private int average(int a, int b) {
        return (a + b) / 2;
    }

    private int randomize(int min, int max) {
        return (int) (Math.random() % (max - min) + min);
    }

    /**
     * Generate multiple heightmaps with different roughness and save them to files.
     *
     * @param count
     */
    public void generateMultipleHeightmaps(int count) {
        for (int i = 0; i < count; i++) {
            // random roughness
            roughness = (float) (Math.random() * 0.05);
            genHeightmap(roughness, "heightmap" + i + "_roughness" + roughness);
        }
    }

    public int[][] getHeightmap() {
        return heightmap;
    }

    public void loadHeightmap(String path) {
        // TODO
    }

    public int[][] getChunk(ChunkCoordinates coords) throws Exception{
        int[][] chunk = new int[CHUNK_WIDTH][CHUNK_WIDTH];
        int i;
        int j;
        // from bottom left to top right
        for (i = (int) coords.getBottomLeft().x; i < coords.getTopRight().x; i++) {
            for (j = (int) coords.getBottomLeft().y; j < coords.getTopRight().y; j++) {
                try {
                    chunk[i - (int) coords.getBottomLeft().x][j - (int) coords.getBottomLeft().y] = heightmap[i][j];
                } catch (Exception e) {
                    throw new Exception("Chunk coordinates out of bounds");
                }
            }
        }
        return chunk;
    }

    @Deprecated
    private void drawChunksCoords(List<ChunkCoordinates> chunksCoords, int size) {
        BufferedImage image = new BufferedImage(World.WORLD_SIZE, World.WORLD_SIZE, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, World.WORLD_SIZE, World.WORLD_SIZE);
        g2d.setColor(Color.WHITE);
        for (ChunkCoordinates coords : chunksCoords) {
            g2d.drawRect((int) coords.getBottomLeft().x, (int) coords.getBottomLeft().y, size, size);
        }
        g2d.dispose();
        try {
            ImageIO.write(image, "png", new File("chunks.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
