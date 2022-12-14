package fr.game.core.entity.terrain;

import fr.game.core.ObjectLoader;
import fr.game.core.entity.Model;
import fr.game.core.entity.Texture;
import fr.game.core.maths.BaseCube;
import fr.game.core.maths.Heightmap;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Chunk {
    private static final int CHUNK_SIZE = 16;

    /**
     * Chunk ID
     */
    private String uuid;

    private final ChunkCoordinates coords;
    Model model;
    int[][] heightmapChunk = new int[CHUNK_SIZE][CHUNK_SIZE];

    public Chunk(ChunkCoordinates coords, Heightmap heightmap) {
        // gen uuid
        this.uuid = java.util.UUID.randomUUID().toString();
        this.coords = coords;
        try {
            this.heightmapChunk = heightmap.getChunk(coords);
        } catch (Exception e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }

    public List<Vector3f> generateChunk() throws Exception {
        // set the heightmap part
        List<Vector3f> positions = new ArrayList<>();

        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                int height = heightmapChunk[i][j];
                int hmNormal = height < 0 ? height * -1 : height;
                for (int k = 0; k < hmNormal + 30; k++) {
                    // heightmap can be under 0, so we need to add the heightmap value to the y
                    // position
                    positions.add(new Vector3f(i, k, j));
                }
            }
        }

        return positions;
    }

    public void generateModel(List<Vector3f> positions) throws Exception {
        // Stockez chaque modèle de bloc dans une liste
        List<Model> blockModels = new ArrayList<>();
        for (Vector3f position : positions) {
            // Générez un modèle pour chaque position de bloc
            BaseCube bc = new BaseCube();
            List<Vector3f> pos = new ArrayList<>() {{
                add(position);
            }};
            Model blockModel = bc.generate(pos);
            blockModels.add(blockModel);
        }

        // Chargez la texture et appliquez-la à chaque modèle de bloc
        ObjectLoader loader = new ObjectLoader();
        int textureID = loader.loadTexture("textures/dirt_block.png");
        Texture dirtTexture = new Texture(textureID);
        for (Model blockModel : blockModels) {
            blockModel.setTexture(dirtTexture);
        }
    }

    /*
    public void generateModel(List<Vector3f> positions) throws Exception {
        // check if the position is a cube
        if (positions.size() > 0) {
            BaseCube bc = new BaseCube();
            model = bc.generate(positions);
        }
        // load texture
        ObjectLoader loader = new ObjectLoader();
        int textureID = loader.loadTexture("textures/dirt_block.png");
        Texture dirtTexture = new Texture(textureID);
        model.setTexture(dirtTexture);
    }*/

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
