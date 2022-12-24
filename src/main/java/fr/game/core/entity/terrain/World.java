package fr.game.core.entity.terrain;

import fr.game.core.listener.ChunkListener;
import fr.game.core.maths.ExecutionMeasure;
import fr.game.core.maths.GFS;
import fr.game.core.maths.Heightmap;
import me.tongfei.progressbar.ProgressBar;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    // World size in chunks
    public static final int WORLD_SIZE = 1000;

    private List<Chunk> chunks;

    // generate heightmap for the world
    Heightmap heightmap;
    private ChunkListener chunkListener;

    public World() {
        this.chunks = new ArrayList<>();
        Random rnd = new Random();
        long seed = rnd.nextLong();
        this.heightmap = new Heightmap(WORLD_SIZE, seed);
    }

    Vector3f coords = new Vector3f();

    public void updateChunks(Vector3f coordinates) {

        return;

        // if old coords are different from new coords
        /*
        if (coords.equals(coordinates)) return;


        ChunkCoordinates[][] chunkCoordinates = heightmap.splitHeightmapGFS(Chunk.getChunkSize());
        GFS gfs = new GFS(chunkCoordinates.length, chunkCoordinates);

        List<ChunkCoordinates> chunksToRender = gfs.getNeighbours((int)coordinates.x, (int)coordinates.z, 2);

        // remove chunks that are already rendered
        chunksToRender.removeIf(chunkCoordinates1 -> chunks.stream().anyMatch(chunk -> chunk.getCoords().equals(chunkCoordinates1)));

        System.out.println("Chunks to render: " + chunksToRender.size());

        // add new chunks
        for (ChunkCoordinates coords : chunksToRender) {
            Chunk chunk = new Chunk(coords, heightmap);
            try {
                chunk.generateChunk();
            } catch (Exception ignored) {
            }
            chunks.add(chunk);
            this.chunkListener.onUpdate(chunk);
        }*/
    }

    /**
     * TODO: Optimize the rendering, (too much time)
     */
    public void init(ChunkListener chunkListener) throws Exception {
        heightmap.genHeightmap();

        this.chunkListener = chunkListener;

        ChunkCoordinates[][] chunkCoordinates = heightmap.splitHeightmapGFS(Chunk.getChunkSize());
        GFS gfs = new GFS(chunkCoordinates.length, chunkCoordinates);

        // Position X, Y, radius 2 = 32
        List<ChunkCoordinates> ccToRender = gfs.getNeighbours(0, 0, 10);
        ProgressBar pb = new ProgressBar("Generating chunks", ccToRender.size());
        for (ChunkCoordinates coordinates : ccToRender) {
            Chunk chunk = new Chunk(coordinates, heightmap);
            List<Vector3f> positions = null;
            try {
                positions = chunk.generateChunk();
            } catch (Exception ignored) {
            }
            chunks.add(chunk);
            this.chunkListener.onUpdate(chunk, positions);
            pb.step();
        }
        pb.close();
    }

    public void save() {
        // save heightmap to file
        heightmap.saveHeightmap("world.heightmap");
    }

    public void load() {
        // load heightmap from file
        // TODO
        heightmap.loadHeightmap("world.heightmap");
    }

    public List<Chunk> getChunks() {
        return chunks;
    }
}
