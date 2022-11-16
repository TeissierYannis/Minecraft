package fr.game.core.entity;

import fr.game.core.entity.terrain.Terrain;
import fr.game.core.lightning.DirectionalLight;
import fr.game.core.lightning.PointLight;
import fr.game.core.lightning.SpotLight;
import fr.game.core.utils.Consts;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    private List<Entity> entities;
    private List<Terrain> terrains;

    private Vector3f ambientLight;
    private SpotLight[] spotLights;
    private PointLight[] pointLights;
    private DirectionalLight[] directionalLights;

    private float lightAngle;
    private float spotAngle = 0;
    private float spotInc = 1;

    public SceneManager(float lightAngle) {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
        ambientLight = Consts.AMBIENT_LIGHT;

        directionalLights = new DirectionalLight[Consts.MAX_DIRECTIONAL_LIGHTS];
        pointLights = new PointLight[Consts.MAX_POINT_LIGHTS];
        spotLights = new SpotLight[Consts.MAX_SPOT_LIGHTS];

        this.lightAngle = lightAngle;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void setTerrains(List<Terrain> terrains) {
        this.terrains = terrains;
    }

    public void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setAmbientLight(float x, float y, float z) {
        this.ambientLight = new Vector3f(x, y, z);
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public DirectionalLight[] getDirectionalLight() {
        return directionalLights;
    }

    public void setDirectionalLight(DirectionalLight[] directionalLight) {
        this.directionalLights = directionalLight;
    }

    public float getLightAngle() {
        return lightAngle;
    }

    public void setLightAngle(float lightAngle) {
        this.lightAngle = lightAngle;
    }

    public float getSpotAngle() {
        return spotAngle;
    }

    public void setSpotAngle(float spotAngle) {
        this.spotAngle = spotAngle;
    }

    public float getSpotInc() {
        return spotInc;
    }

    public void setSpotInc(float spotInc) {
        this.spotInc = spotInc;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addDirectionalLight(DirectionalLight directionalLight) {
        for (int i = 0; i < Consts.MAX_DIRECTIONAL_LIGHTS; i++) {
            if (this.directionalLights[i] == null) {
                this.directionalLights[i] = directionalLight;
                return;
            }
        }
    }

    public void addPointLight(PointLight pointLight) {
        for (int i = 0; i < Consts.MAX_POINT_LIGHTS; i++) {
            if (this.pointLights[i] == null) {
                this.pointLights[i] = pointLight;
                return;
            }
        }
    }

    public void addSpotLight(SpotLight spotLight) {
        for (int i = 0; i < Consts.MAX_SPOT_LIGHTS; i++) {
            if (this.spotLights[i] == null) {
                this.spotLights[i] = spotLight;
                return;
            }
        }
    }

    public void incSpotAngle(float inc) {
        spotAngle += inc;
    }

    public void incLightAngle(float inc) {
        lightAngle += inc;
    }

    public void moveSpotLight(SpotLight spotLight, Vector3f vector3f) {
        // find spot light
        for (int i = 0; i < Consts.MAX_SPOT_LIGHTS; i++) {
            if (this.spotLights[i] == spotLight) {
                this.spotLights[i].getPointLight().getPosition().add(vector3f);
                return;
            }
        }
    }

    public void setSpotLightPosition(SpotLight spotLight, Vector3f vector3f) {
        // find spot light
        for (int i = 0; i < Consts.MAX_SPOT_LIGHTS; i++) {
            if (this.spotLights[i] == spotLight) {
                this.spotLights[i].getPointLight().getPosition().set(vector3f);
                return;
            }
        }
    }
}
