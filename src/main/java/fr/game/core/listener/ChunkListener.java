package fr.game.core.listener;

import fr.game.core.entity.terrain.Chunk;
import org.joml.Vector3f;

import java.util.List;

public interface ChunkListener {

    void onUpdate(Chunk chunk, List<Vector3f> positions) throws Exception;
}
