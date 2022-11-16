package fr.game.core.entity;

import fr.game.core.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Model {
    private int id;
    private int vertexCount;
    private Material material;

    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indices;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }

    public Model(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
    }

    public Model(Model model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
        this.material.setTexture(texture);
    }

    public Model(int id, int length, float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.id = id;
        this.vertexCount = length;
        this.material = new Material();
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
    }

    public Model(int id, int length, float[] vertices, float[] textureCoords, float[] normals, int[] indices, Texture texture) {
        this.id = id;
        this.vertexCount = length;
        this.material = new Material();
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.material.setTexture(texture);
    }

    public Material getMaterial() {
        return material;
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Texture getTexture() {
        return material.getTexture();
    }

    public void setTexture(Texture texture) {
        this.material.setTexture(texture);
    }

    public void setTexture(Texture texture, float reflectance) {
        this.material.setTexture(texture);
        this.material.setReflectance(reflectance);
    }

    public void setTexture(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture) {
        this.material.setTexture(texture);
        this.material.setAmbientColor(ambientColor);
        this.material.setDiffuseColor(diffuseColor);
        this.material.setSpecularColor(specularColor);
        this.material.setReflectance(reflectance);
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }

    public void exportToOBJ(String path) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Exported from Java");
        lines.add("#v (vertices) : " + vertices.length);
        lines.add("#vt (texture coords) : " + textureCoords.length);
        lines.add("#vn (normals) : " + normals.length);
        lines.add("#f (faces) : " + indices.length);

        lines.add("mtllib " + path + ".mtl");
        lines.add("o " + path);

        // Check all vertices not used in indices
        ArrayList<Integer> unusedVertices = new ArrayList<>();

        for (int i = 0; i < vertices.length; i++) {
            boolean used = false;
            for (int j = 0; j < indices.length; j++) {
                if (indices[j] == i) {
                    used = true;
                    break;
                }
            }
            if (!used) {
                unusedVertices.add(i);
            }
        }

// Add vertices
        for (int i = 0; i < vertices.length; i++) {
            lines.add("v " + vertices[i] + " " + vertices[i + 1] + " " + vertices[i + 2]);
            i += 2;
        }

        // Check for all unused texture coords
        ArrayList<Integer> unusedTextureCoords = new ArrayList<>();

        for (int i = 0; i < textureCoords.length; i++) {
            boolean used = false;
            for (int j = 0; j < indices.length; j++) {
                if (indices[j] == i) {
                    used = true;
                    break;
                }
            }
            if (!used) {
                unusedTextureCoords.add(i);
            }
        }

        // Add texture coords
        for (int i = 0; i < textureCoords.length; i++) {
            lines.add("vt " + textureCoords[i] + " " + textureCoords[i + 1]);
            i += 1;
        }

        // Check for all unused normals
        ArrayList<Integer> unusedNormals = new ArrayList<>();

        for (int i = 0; i < normals.length; i++) {
            boolean used = false;
            for (int j = 0; j < indices.length; j++) {
                if (indices[j] == i) {
                    used = true;
                    break;
                }
            }
            if (!used) {
                unusedNormals.add(i);
            }
        }

        // Add normals
        for (int i = 0; i < normals.length; i++) {
            lines.add("vn " + normals[i] + " " + normals[i + 1] + " " + normals[i + 2]);
            i += 2;
        }

        lines.add("usemtl " + path);
        int start = 1;

        for (int i = 0; i < indices.length; i += 3) {
            if (i % 6 == 0) {
                lines.add("s " + start);
                start++;
            }
            lines.add("f " + (indices[i] + 1) + "/" + (indices[i] + 1) + "/" + (indices[i] + 1) + " " + (indices[i + 1] + 1) + "/" + (indices[i + 1] + 1) + "/" + (indices[i + 1] + 1) + " " + (indices[i + 2] + 1) + "/" + (indices[i + 2] + 1) + "/" + (indices[i + 2] + 1));
        }


        Utils.writeLines(path + ".obj", lines);

    }
}
