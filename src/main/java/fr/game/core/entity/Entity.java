package fr.game.core.entity;

import org.joml.Vector3f;

public class Entity {
    private Model model;
    private Vector3f position, rotation;
    private float scale;


    public Entity(Model mode, Vector3f position, Vector3f rotation, float scale) {
        this.model = mode;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void incrementPosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void incrementRotation(float dx, float dy, float dz) {
        this.rotation.x += dx;
        this.rotation.y += dy;
        this.rotation.z += dz;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public void exportToOBJ(String path) {
        model.exportToOBJ(path);
    }
}
