package fr.game.core.entity.terrain;

import org.joml.Vector2f;

public class ChunkCoordinates {

    Vector2f bottomLeft;
    Vector2f bottomRight;
    Vector2f topLeft;
    Vector2f topRight;

    public ChunkCoordinates(Vector2f bottomLeft, Vector2f bottomRight, Vector2f topLeft, Vector2f topRight) {
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.topRight = topRight;
    }

    public Vector2f getBottomLeft() {
        return bottomLeft;
    }

    public Vector2f getBottomRight() {
        return bottomRight;
    }

    public Vector2f getTopLeft() {
        return topLeft;
    }

    public Vector2f getTopRight() {
        return topRight;
    }

    @Override
    public String toString() {
        return "ChunkCoordinates{" +
                "bottomLeft=" + bottomLeft +
                ", bottomRight=" + bottomRight +
                ", topLeft=" + topLeft +
                ", topRight=" + topRight +
                '}';
    }
}
