package fr.game.core.maths;

public class Vertex {
    private float[] vertices;

    int count;

    public Vertex(float[] vertices) {
        this.vertices = vertices;
        this.count = vertices.length;
    }

    public float[] getVertices() {
        return vertices;
    }
}
