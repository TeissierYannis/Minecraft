package fr.game.core;

import de.matthiasmann.twl.utils.PNGDecoder;
import fr.game.core.entity.Entity;
import fr.game.core.entity.Model;
import fr.game.core.entity.Texture;
import fr.game.core.maths.Normal;
import fr.game.core.maths.TextureCoords;
import fr.game.core.maths.Vertex;
import fr.game.core.utils.Utils;
import fr.game.test.Launcher;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ObjectLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadOBJModelFromFile(String filename) throws Exception {
        List<String> lines = Utils.readAllLines(filename);

        return loadOBJModelFromStringList(lines);
    }

    public Model loadOBJModelFromString(String object) throws Exception {
        List<String> lines = List.of(object.split("\n"));

        return loadOBJModelFromStringList(lines);
    }

    private Model loadOBJModelFromStringList(List<String> lines) throws Exception {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    vertices.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                    break;
                case "vn":
                    normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                    break;
                case "vt":
                    textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                    break;
                case "f":
                    // TODO : handle faces with more than 3 vertices
                    for (int i = 1; i < tokens.length; i++) {
                        processFace(tokens[i], faces);
                    }
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[faces.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices) {
            verticesArray[i * 3] = pos.x;
            verticesArray[i * 3 + 1] = pos.y;
            verticesArray[i * 3 + 2] = pos.z;
            i++;
        }
        float[] texCoordArr = new float[faces.size() * 2];
        float[] normalsArr = new float[faces.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalsArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        // TODO: Verify this one
        // from main thread

        return loadModel(verticesArray, texCoordArr, normalsArr, indicesArr);
    }

    public static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList, List<Vector3f> normalList, List<Integer> indicesList, float[] texCoordArr, float[] normalArr) {

        indicesList.add(pos);

        if (texCoord >= 0) {
            Vector2f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }

        if (normal >= 0) {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if (length > 1) {
            String textCoord = lineToken[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : -1;
            if (length > 2) {
                normal = Integer.parseInt(lineToken[2]) - 1;
            }
        }
        Vector3i face = new Vector3i(pos, coords, normal);
        faces.add(face);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) throws Exception {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);
        unbind();
        return new Model(id, indices.length, vertices, textureCoords, normals, indices);
    }

    public Model loadModel(Entity[] entities) {
        // process entity to create obj file
        // V -> vertex
        // VT -> texture
        // VN -> normal
        // F -> face (V/VT/VN)

        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<TextureCoords> textures = new ArrayList<>();
        List<Normal> normals = new ArrayList<>();

        int modelIndex = 1;
        for (Entity entity : entities) {
            Model model = entity.getModel();
            for (int i = 0; i < model.getVertices().length / 3; i += 3) {
                Vertex vertex = new Vertex(
                        new float[]{model.getVertices()[i], model.getVertices()[i + 1], model.getVertices()[i + 2]}
                );
                vertices.add(vertex);
            }

            for (int i = 0; i < model.getTextureCoords().length / 2; i += 2) {
                TextureCoords textureCoords = new TextureCoords(
                        model.getTextureCoords()[i],
                        model.getTextureCoords()[i + 1]
                );
                textures.add(textureCoords);
            }

            for (int i = 0; i < model.getNormals().length / 3; i += 3) {
                Normal normal = new Normal(
                        model.getNormals()[i],
                        model.getNormals()[i + 1],
                        model.getNormals()[i + 2]
                );
                normals.add(normal);
            }

            for (int i = 0; i < model.getIndices().length; i++) {
                indices.add(model.getIndices()[i] + (modelIndex));
            }
            modelIndex++;
        }

        // TODO : create obj file
        String obj = "# Blender v2.79 (sub 0) OBJ File: ''\n" +
                "# www.blender.org\n" +
                "mtllib " + "test" + ".mtl\n" +
                "o " + "test" + "\n";
        for (Vertex vertex : vertices) {
            obj += "v " + vertex.getVertices()[0] + " " + vertex.getVertices()[1] + " " + vertex.getVertices()[2];
            obj += "\n";
        }
        obj += "\n";
        for (TextureCoords textureCoords : textures) {
            obj += "vt " + textureCoords.getTextureCoords().x + " " + textureCoords.getTextureCoords().y;
            obj += "\n";
        }
        obj += "\n";
        for (Normal normal : normals) {
            obj += "vn " + normal.getNormal().x + " " + normal.getNormal().y + " " + normal.getNormal().z;
            obj += "\n";
        }
        obj += "\n";
        for (int i = 0; i < indices.size(); i += 3) {
            obj += "f " + indices.get(i) + "/" + indices.get(i) + "/" + indices.get(i) + " " + indices.get(i + 1) + "/" + indices.get(i + 1) + "/" + indices.get(i + 1) + " " + indices.get(i + 2) + "/" + indices.get(i + 2) + "/" + indices.get(i + 2);
            obj += "\n";
        }
        File file = new File("test.obj");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(obj);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO : load obj file
        try {
            return loadOBJModelFromFile("test.obj");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Texture texture) throws Exception {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);
        unbind();
        return new Model(id, indices.length, vertices, textureCoords, normals, indices, texture);
    }

    public int loadTexture(String fileName) throws Exception {

        try (InputStream inputStream = ObjectLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            PNGDecoder decoder = new PNGDecoder(inputStream);
            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();

            int id = glGenTextures();
            this.textures.add(id);

            glBindTexture(GL_TEXTURE_2D, id);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            return id;
        } catch (IOException throwable) {
            throwable.printStackTrace();
            System.err.printf("Texture(%s) not found.%n", fileName);
        }
        return -1;
    }

    @Deprecated
    public int loadTexture0(String fileName) throws Exception {

        int width, height;
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(fileName, w, h, c, 4);
            if (buffer == null)
                throw new Exception("Image File: " + fileName + " not loaded. Info: " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    private int createVAO() throws Exception {
        int vaoId = GL30.glGenVertexArrays();
        vaos.add(vaoId);
        GL30.glBindVertexArray(vaoId);
        return vaoId;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {

        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for (int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for (int texture : textures)
            GL30.glDeleteTextures(texture);
    }
}