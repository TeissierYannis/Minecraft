package fr.game.core.maths;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextureCoords {

    Vector2f textureCoords;

    public TextureCoords(float x, float y) {
        this.textureCoords = new Vector2f(x, y);
    }

    public TextureCoords(Vector2f textureCoords) {
        this.textureCoords = textureCoords;
    }

    public Vector2f getTextureCoords() {
        return textureCoords;
    }

}

