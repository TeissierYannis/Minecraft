package fr.game.core.entity.terrain;

import fr.game.core.maths.Heightmap;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class World {
    // World size in chunks
    public static final int WORLD_SIZE = 1000;

    private final int baseChunks = 9;

    private List<Chunk> chunks;

    // generate heightmap for the world
    Heightmap heightmap;

    public World() {
        this.chunks = new ArrayList<>();

        Random rnd = new Random();

        long seed = rnd.nextLong();

        this.heightmap = new Heightmap(WORLD_SIZE, seed);
    }

    public void init() throws Exception {
        heightmap.genHeightmap();

        List<ChunkCoordinates> chunkCoordinates = heightmap.splitHeightmap(Chunk.getChunkSize());

        System.out.println("\nGenerating chunks... [" + chunkCoordinates.size() + " chunks]");
        int i = 0;
        ProgressBar pb = new ProgressBar("Generating chunks", chunkCoordinates.size());
        for (ChunkCoordinates coords : chunkCoordinates) {
            if (i > 20) {
                break;
            } else {
                System.out.println("Generating chunk " + (int)coords.getBottomLeft().x + " " + (int)coords.getBottomLeft().y);
            }
            Chunk chunk = new Chunk(coords, heightmap);
            try {
                chunk.generateChunk();
                chunks.add(chunk);
            } catch (Exception e) {
                Logger.getLogger(World.class.getName()).severe(e.getMessage());
                break;
            }
            pb.stepTo(chunks.size());
            pb.setExtraMessage("Chunks generated: " + chunks.size() + "/" + chunkCoordinates.size());
            i++;
        }
        pb.close();
        /**
         *
         int x = (int) startChunkCoords.x;
         int z = (int) startChunkCoords.y;

         // gen chunks
         for (int i = 0; i < baseChunks; i++) {
         Chunk chunk = new Chunk(x, z);
         chunk.generateChunk(heightmap);
         chunks.add(chunk);
         x += Chunk.getChunkSize() + 1;
         z += Chunk.getChunkSize() + 1;
         }
         */

    }

    public void save() {
        // save heightmap to file
        heightmap.saveHeightmap("world.heightmap");
    }

    public void load() {
        // load heightmap from file
        heightmap.loadHeightmap("world.heightmap");
    }

    public List<Chunk> getChunks() {
        return chunks;
    }
}
