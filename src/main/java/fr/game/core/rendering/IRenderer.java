package fr.game.core.rendering;

import fr.game.core.entity.Camera;
import fr.game.core.entity.Model;
import fr.game.core.lightning.DirectionalLight;
import fr.game.core.lightning.PointLight;
import fr.game.core.lightning.SpotLight;

public interface IRenderer<T> {

    void init() throws Exception;

    void render(Camera camera, DirectionalLight[] directionalLights, PointLight[] pointLights, SpotLight[] spotLights);

    void bind(Model model);

    void unbind(Model model);

    void prepare(T entity, Camera camera);

    void cleanup();
}
