package fr.game.core.maths;

import fr.game.core.ObjectLoader;
import fr.game.core.entity.Model;
import me.tongfei.progressbar.ProgressBar;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class BaseCube {


    Vector3f position = new Vector3f(0, 0, 0);

    // vertex are (v)
    float[] vertex = new float[]{
            0, 0, 1, // v0
            1, 0, 1, // v1
            0, 1, 1, // v2
            1, 1, 1, // v3
            0, 1, 0, // v4
            1, 1, 0, // v5
            0, 0, 0, // v6
            1, 0, 0 // v7
    };

    float[] baseVertex = new float[]{
            0, 0, 1, // v0
            1, 0, 1, // v1
            0, 1, 1, // v2
            1, 1, 1, // v3
            0, 1, 0, // v4
            1, 1, 0, // v5
            0, 0, 0, // v6
            1, 0, 0 // v7
    };

    // textures coords are (vt)
    float[] texturesCoords = new float[]{
            0, 0, // vt0
            1, 0, // vt1
            0, 1, // vt2
            1, 1, // vt3
    };

    // Normals are (vn)
    float[] normals = new float[]{
            0, 0, 1, // vn0
            0, 1, 0, // vn1
            0, 0, -1, // vn2
            0, -1, 0, // vn3
            1, 0, 0, // vn4
            -1, 0, 0, // vn5
    };

    // Faces are : (f) => vertex (v), texture (vt), normal (vn)
    int[] faces = new int[]{
            1, 1, 1, 2, 2, 1, 3, 3, 1, // f0
            3,3,1,2,2,1,4,4,1, // f1
            3,1,2,4,2,2,5,3,2, // f2
            5,3,2,4,2,2,6,4,2, // f3
            5,4,3,6,3,3,7,2,3,
            7,2,3,6,3,3,8,1,3,
            7,1,4,8,2,4,1,3,4,
            1,3,4,8,2,4,2,4,4,
            2,1,5,8,2,5,4,3,5,
            4,3,5,8,2,5,6,4,5,
            7,1,6,1,2,6,5,3,6,
            5,3,6,1,2,6,3,4,6
    };

    public BaseCube(Vector3f position) {
        this.position = position;
    }

    public BaseCube() {
    }

    public void exportToObj(String path) {
        String obj = "# Exported from Java\n";
        obj += "#v (vertices) : " + vertex.length + "\n";
        obj += "#vt (texture coords) : " + texturesCoords.length + "\n";
        obj += "#vn (normals) : " + normals.length + "\n";
        obj += "#f (faces) : " + faces.length + "\n";

        obj += "mtllib " + path + ".mtl\n";
        obj += "o " + path + "\n";

        for (int i = 0; i < vertex.length; i += 3) {
            obj += "v " + vertex[i] + " " + vertex[i + 1] + " " + vertex[i + 2] + "\n";
        }

        for (int i = 0; i < texturesCoords.length; i += 2) {
            obj += "vt " + texturesCoords[i] + " " + texturesCoords[i + 1] + "\n";
        }

        for (int i = 0; i < normals.length; i += 3) {
            obj += "vn " + normals[i] + " " + normals[i + 1] + " " + normals[i + 2] + "\n";
        }

        for (int i = 0; i < faces.length; i += 12) {
            obj += "f " + faces[i] + "/" + faces[i + 1] + "/" + faces[i + 2] + " " + faces[i + 3] + "/" + faces[i + 4] + "/" + faces[i + 5] + " " + faces[i + 6] + "/" + faces[i + 7] + "/" + faces[i + 8] + " " + faces[i + 9] + "/" + faces[i + 10] + "/" + faces[i + 11] + "\n";
        }

        try {
            Files.write(Paths.get(path + ".obj"), obj.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportToObj(String path, Vector3f position) {
        moveVertices(position);

        exportToObj(path);
    }

    public void exportMultipleCubes(String path, List<Vector3f> positions) {
        // measure time to export
        long startTime = System.currentTimeMillis();

        String obj = generateString(positions);

        Logger.getGlobal().info("Exporting to file");
        try {
            Files.write(Paths.get(path + ".obj"), obj.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.getGlobal().info("Exported to file");
        // measure time to export
        long endTime = System.currentTimeMillis();
        Logger.getGlobal().info("Exported " + positions.size() + " cubes to " + path + ".obj in " + (endTime - startTime) + "ms");
    }

    public Model generate(List<Vector3f> positions) throws Exception {
        String object = generateString(positions);

        ObjectLoader ol = new ObjectLoader();

        return ol.loadOBJModelFromString(object);
    }

    public String generateString(List<Vector3f> positions) {
        String obj = "# Exported from Java\n";
        obj += "#v (vertices) : " + vertex.length + "\n";
        obj += "#vt (texture coords) : " + texturesCoords.length + "\n";
        obj += "#vn (normals) : " + normals.length + "\n";
        obj += "#f (faces) : " + faces.length + "\n";

        obj += "mtllib chunk.mtl\n";
        obj += "o chunk\n";

        AtomicBoolean verticesExported = new AtomicBoolean(false);
        AtomicBoolean texturesCoordsExported = new AtomicBoolean(false);
        AtomicBoolean normalsExported = new AtomicBoolean(false);
        AtomicBoolean facesExported = new AtomicBoolean(false);

        AtomicReference<String>[] vertexString = new AtomicReference[]{new AtomicReference<>("")};
        AtomicReference<String> texturesCoordsString = new AtomicReference<>("");
        AtomicReference<String> normalsString = new AtomicReference<>("");
        AtomicReference<String> facesString = new AtomicReference<>("");

        new Thread(() -> {
            for (Vector3f position : positions) {
                moveVertices(position);

                for (int i = 0; i < vertex.length; i += 3) {
                    vertexString[0].set(vertexString[0].get() + "v " + vertex[i] + " " + vertex[i + 1] + " " + vertex[i + 2] + "\n");
                }
                vertexString[0].set(vertexString[0].get() + "\n");
            }
            verticesExported.set(true);
        }).start();
        new Thread(() -> {
            for (int i = 0; i < texturesCoords.length; i += 2) {
                texturesCoordsString.set(texturesCoordsString.get() + "vt " + texturesCoords[i] + " " + texturesCoords[i + 1] + "\n");
            }
            texturesCoordsString.set(texturesCoordsString.get() + "\n");
            texturesCoordsExported.set(true);
        }).start();
        new Thread(() -> {
            for (int i = 0; i < normals.length; i += 3) {
                normalsString.set(normalsString.get() + "vn " + normals[i] + " " + normals[i + 1] + " " + normals[i + 2] + "\n");
            }
            normalsString.set(normalsString.get() + "\n");
            normalsExported.set(true);
        }).start();

        new Thread(() -> {
            //ProgressBar bar = new ProgressBar("Generating faces", (long) positions.size() * (faces.length / 2));

            int offset = 1;
            int offsetFaces = 1;
            for (Vector3f position : positions) {
                for (int i = 0; i < faces.length; i += 3) {
                    if (i % 12 == 0) {
                        facesString.set(facesString.get() + "\ns " + offsetFaces);
                        facesString.set(facesString.get() + "\nf ");
                        offsetFaces++;
                    }
                    facesString.set(facesString.get() + (calculateFace(offset, faces[i])) + "/" + faces[i + 1] + "/" + faces[i + 2] + " ");
                    //bar.step();
                }
                offset++;
            }
            //bar.setExtraMessage("Done");
            facesExported.set(true);
        }).start();


        while (!verticesExported.get() || !texturesCoordsExported.get() || !normalsExported.get() || !facesExported.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check thread status
        if (verticesExported.get() && texturesCoordsExported.get() && normalsExported.get() && facesExported.get()) {
            obj += vertexString[0].get();
            obj += texturesCoordsString.get();
            obj += normalsString.get();
            obj += "\nusemtl Material\ns off\n";
            obj += facesString.get();
        } else {
            Logger.getGlobal().warning("Exporting failed");
        }

        return obj;
    }

    public int calculateFace(int offset, int face) {
        return offset * (vertex.length / 3) - (vertex.length / 3) + face;
    }

    public void moveVertices(Vector3f newPosition) {
        // difference from coords 0
        float x = newPosition.x - position.x;
        float y = newPosition.y - position.y;
        float z = newPosition.z - position.z;

        for (int i = 0; i < vertex.length; i += 3) {
            vertex[i] = baseVertex[i] + x;
            vertex[i + 1] = baseVertex[i + 1] + y;
            vertex[i + 2] = baseVertex[i + 2] + z;
        }
    }
}
