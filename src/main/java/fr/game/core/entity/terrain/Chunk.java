package fr.game.core.entity.terrain;

import fr.game.core.entity.Model;
import fr.game.core.maths.BaseCube;
import fr.game.core.maths.Heightmap;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Chunk {

    /**
     * X,Y,Z
     */
    private static final int CHUNK_SIZE = 16;
    private final ChunkCoordinates coords;

    Model model;

    int heightmapChunk[][] = new int[CHUNK_SIZE][CHUNK_SIZE];

    public Chunk(ChunkCoordinates coords, Heightmap heightmap) throws Exception {
        this.coords = coords;
        try {
            this.heightmapChunk = heightmap.getChunk(coords);
        } catch (Exception e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }

    public void generateChunk() throws Exception {

        // set the heightmap part

        List<Vector3f> positions = new ArrayList<>();

        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                int height = heightmapChunk[i][j];
                int hmNormal = height < 0 ? height * -1 : height;
                for (int k = 0; k < hmNormal; k++) {
                    // heightmap can be under 0, so we need to add the heightmap value to the y
                    // position
                    positions.add(new Vector3f(i, k, j));
                }
            }
        }

        // check if the position is a cube
        if (positions.size() > 0) {
            BaseCube bc = new BaseCube();
            model = bc.generate(positions);
        }
    }

    public static int getChunkSize() {
        return CHUNK_SIZE;
    }

    public ChunkCoordinates getCoords() {
        return coords;
    }

    public Model getModel() {
        return model;
    }
}
