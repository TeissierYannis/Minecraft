package fr.game.core.rendering;

import fr.game.core.ShaderManager;
import fr.game.core.WindowManager;
import fr.game.core.entity.Camera;
import fr.game.core.entity.Entity;
import fr.game.core.entity.SceneManager;
import fr.game.core.entity.terrain.Terrain;
import fr.game.core.lightning.DirectionalLight;
import fr.game.core.lightning.PointLight;
import fr.game.core.lightning.SpotLight;
import fr.game.core.utils.Consts;
import fr.game.test.Launcher;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;

    private static boolean isCulling = false;

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() throws Exception {
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();
        entityRenderer.init();
        terrainRenderer.init();
    }

    public static void renderLights(DirectionalLight[] directionalLights, PointLight[] pointLights, SpotLight[] spotLights, ShaderManager shader) {
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);

        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            if (spotLights[i] != null) {
                shader.setUniform("spotLights", spotLights[i], i);
            }
        }
        numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            if (pointLights[i] != null) {
                shader.setUniform("pointLights", pointLights[i], i);
            }
        }
        numLights = directionalLights != null ? directionalLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            if (directionalLights[i] != null) {
                shader.setUniform("directionalLights", directionalLights[i], i);
            }
        }
    }

    public void render(Camera camera, SceneManager sceneManager) {
        clear();

        if (window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        entityRenderer.renderEntitiesInRadius(camera, 1f, sceneManager.getDirectionalLight(), sceneManager.getPointLights(), sceneManager.getSpotLights());
        terrainRenderer.render(camera, sceneManager.getDirectionalLight(), sceneManager.getPointLights(), sceneManager.getSpotLights());
    }

    public static void enableCulling() {
        if (!isCulling) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            isCulling = true;
        }
    }

    public static void disableCulling() {
        if (isCulling) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            isCulling = false;
        }
    }

    public void processEntity(Entity entity) {
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if (entityList != null) {
            entityList.add(entity);
        } else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrainRenderer.getTerrains().add(terrain);
    }

    public void clear() {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
    }
}
