package fr.game.test;

import fr.game.core.*;
import fr.game.core.entity.*;
import fr.game.core.entity.terrain.*;
import fr.game.core.lightning.DirectionalLight;
import fr.game.core.lightning.PointLight;
import fr.game.core.lightning.SpotLight;
import fr.game.core.rendering.RenderManager;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import static fr.game.core.utils.Consts.CAMERA_MOVE_SPEED;

public class TestGame implements ILogic {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private SceneManager sceneManager;

    private Camera camera;
    private Vector3f cameraInc;


    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        sceneManager = new SceneManager(-90);
    }

    @Override
    public void init() throws Exception {
        /*
        BaseCube bc = new BaseCube();
        List<Vector3f> pos = new ArrayList<>();

        Random random = new Random();

        Heightmap heightmap = new Heightmap(random.nextLong());
        heightmap.genHeightmap();
        heightmap.saveHeightmap("heightmap");

        int[][] hm = heightmap.getHeightmap();

        // create a terrain of 10x10
        for (int x = 0; x < heightmap.CHUNK_WIDTH; x++) {
            for (int z = 0; z < heightmap.CHUNK_WIDTH; z++) {
                for (int y = 0; y < hm[x][z]; y++) {
                    pos.add(new Vector3f(x, y, z));
                }
            }
        }*/
        // TODO : Procedural generation : https://www.youtube.com/watch?v=4hA7G3gup-4
        // Try to generate 10 first chunk, 10 first rows and generate the rest
        // TODO : Add a chunk manager to manage the chunks
        // run in one thread
        //new Thread(() -> bc.exportMultipleCubes("baseCube", pos)).start();

        int textureID = loader.loadTexture("textures/dirt_block.png");
        Texture dirtTexture = new Texture(textureID);

        World world = new World();
        world.init();
        world.save();

        //Model m = loader.loadOBJModelFromFile("/models/baseCube.obj");
        //m.setTexture(dirtTexture);

        //Entity c = new Entity(m, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);

        //sceneManager.addEntity(c);

       /* for (Chunk chunk : world.getChunks()) {
            Model model = chunk.getModel();
            if (model != null) {
                model.setTexture(dirtTexture);
                Entity c = new Entity(model, new Vector3f(chunk.getCoords().getBottomLeft().x, 0, chunk.getCoords().getBottomLeft().y), new Vector3f(0, 0, 0), 1);
                sceneManager.addEntity(c);
            }
        }*/

        //Model model = loader.loadOBJModelFromFile("/models/baseCube.obj");

        // setTexture(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture)
        /*
        model.setTexture(
                new Texture(loader.loadTexture("textures/dirt_block.png")),
                1
        );

        Entity e1 = new Entity(model, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);

        sceneManager.addEntity(e1);*/


        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/dirt_block.png"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTexture("textures/pack.png"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTexture("textures/Grass_Block_TEX.png"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTexture("textures/pack.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap.png"));

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(backgroundTexture, redTexture, greenTexture, blueTexture);

        Terrain terrain = new Terrain(
                new Vector3f(0f, 1f, -800f),
                loader,
                new Material(
                        new Vector4f(0, 0, 0, 0),
                        0.1f
                ),
                blendMapTerrain,
                blendMap
        );
        sceneManager.addTerrain(terrain);

        float lightIntensity = 1f;
        // point light
        Vector3f lightPosition = new Vector3f(2, 2, 2);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity);

        // spot light
        Vector3f coneDirection = new Vector3f(0, -50, 0);
        float cutoff = (float) Math.cos(Math.toRadians(10));
        SpotLight spotLight = new SpotLight(
                new PointLight(new Vector3f(1, 1, 1), new Vector3f(30f, 10f, -30f), 50000f, 0, 0, 0.05f),
                coneDirection, cutoff
        );

        // directipnal light
        lightPosition = new Vector3f(1, 10, -1);
        lightColor = new Vector3f(1, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(lightColor, lightPosition, 1f);

        sceneManager.addDirectionalLight(directionalLight);
        //sceneManager.addPointLight(pointLight);
        sceneManager.addSpotLight(spotLight);

        // ?????? PQ ? renderer.init();
        renderer.init();
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            camera.movePosition(0, 0, CAMERA_MOVE_SPEED);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            camera.movePosition(0, 0, -CAMERA_MOVE_SPEED);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            camera.movePosition(-CAMERA_MOVE_SPEED, 0, 0);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            camera.movePosition(CAMERA_MOVE_SPEED, 0, 0);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            camera.movePosition(0, CAMERA_MOVE_SPEED, 0);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            camera.movePosition(0, -CAMERA_MOVE_SPEED, 0);
        }


    }

    @Override
    public void update(MouseManager mouseInput) {
        camera.movePosition(cameraInc.x, cameraInc.y, cameraInc.z);

        if (mouseInput.isLeftButtonPressed()) {
            if (mouseInput.isLocked()) {
                mouseInput.unlockCursor();
            } else {
                mouseInput.lockCursor();
            }
        }

        if (mouseInput.isLocked()) {
            camera.setRotation(-mouseInput.getVerticalAngle(), mouseInput.getHorizontalAngle(), 0);
        }

        // circle movement of the spot light
        float radius = 50;
        float x = (float) Math.abs(Math.sin(System.currentTimeMillis() / 1000.0) * radius);
        float z = (float) Math.abs(Math.cos(System.currentTimeMillis() / 1000.0) * radius);
        // y navigate between 0 and 50
        float y = (float) Math.abs(Math.sin(System.currentTimeMillis() / 1000.0) * 50);
        sceneManager.setSpotLightPosition(sceneManager.getSpotLights()[0], new Vector3f(x, y, -z));

        for (Entity entity : sceneManager.getEntities()) {
            renderer.processEntity(entity);
        }

        for (Terrain terrain : sceneManager.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, sceneManager);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
