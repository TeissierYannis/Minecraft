package fr.game.core.rendering;

import fr.game.core.ShaderManager;
import fr.game.core.entity.Camera;
import fr.game.core.entity.Entity;
import fr.game.core.entity.Model;
import fr.game.core.lightning.DirectionalLight;
import fr.game.core.lightning.PointLight;
import fr.game.core.lightning.SpotLight;
import fr.game.core.utils.Consts;
import fr.game.core.utils.Transformation;
import fr.game.core.utils.Utils;
import fr.game.test.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements IRenderer {

    ShaderManager shader;
    private Map<Model, List<Entity>> entities;

    public EntityRenderer() throws Exception {
        entities = new HashMap<>();
        shader = new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/entity_vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.glsl"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightListUniform("directionalLights", Consts.MAX_DIRECTIONAL_LIGHTS);
        shader.createPointLightListUniform("pointLights", Consts.MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights", Consts.MAX_SPOT_LIGHTS);
    }

    @Override
    public void render(Camera camera, DirectionalLight[] directionalLights, PointLight[] pointLights, SpotLight[] spotLights) {
        shader.bind();
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
        RenderManager.renderLights(directionalLights, pointLights, spotLights, shader);

        for (Model model : entities.keySet()) {
            bind(model);
            for (Entity e : entities.get(model)) {
                prepare(e, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind(model);
        }
        entities.clear();
        shader.unbind();
    }

    public void renderEntitiesInRadius(Camera camera, float radius, DirectionalLight[] directionalLights, PointLight[] pointLights, SpotLight[] spotLights) {
        shader.bind();
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
        RenderManager.renderLights(directionalLights, pointLights, spotLights, shader);

        for (Model model : entities.keySet()) {
            bind(model);
            for (Entity e : entities.get(model)) {
                // Calculez la distance entre la position de l'entité et la position de la caméra
                // TODO : La distance du chunk est en bas à gauche, ce qui signifie que si l'on monte trop la caméra, on ne verra pas les entités
                float distance = e.getPosition().distance(camera.getPosition());
                // Si la distance est inférieure au rayon spécifié, effectuez le rendu de l'entité
                if (distance <= radius) {
                    prepare(e, camera);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            unbind(model);
        }
        entities.clear();
        shader.unbind();
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if (model.getMaterial().isDisableCulling()) {
            RenderManager.disableCulling();
        } else {
            RenderManager.enableCulling();
        }

        shader.setUniform("material", model.getMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL13.GL_TEXTURE_2D, model.getMaterial().getTexture().getId());
    }

    @Override
    public void unbind(Model model) {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

    public Map<Model, List<Entity>> getEntities() {
        return entities;
    }
}
