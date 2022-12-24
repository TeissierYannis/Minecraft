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
        ExecutionMeasure measure = new ExecutionMeasure();
        // TODO : use a thread pool or something to make it faster
        String object = generateString(positions);
        measure.stop();
        measure.logTime("Generated " + positions.size() + " cubes in ");

        ObjectLoader ol = new ObjectLoader();

        return ol.loadOBJModelFromString(object);
    }

    /*
    Pour accélérer la vitesse de génération d'un string en Java, vous pouvez essayer plusieurs approches différentes :

Utiliser un StringBuilder au lieu d'un String pour concaténer les différentes parties du string. Le StringBuilder est plus rapide que le String car il utilise un buffer interne pour stocker les données, ce qui permet d'éviter la création de nouvelles instances de String à chaque concaténation.

Utiliser la méthode String.format() pour formater le string en une seule opération. Cette méthode est plus rapide que d'utiliser des opérations de concaténation pour construire le string.

Utiliser des algorithmes et des structures de données efficaces pour générer le string. Par exemple, si vous devez générer un string à partir d'une liste d'éléments, vous pouvez utiliser une boucle for ou un Iterator au lieu d'une boucle foreach.

Si possible, pré-allouer la mémoire nécessaire pour stocker le string généré, en utilisant la méthode StringBuilder.ensureCapacity(). Cela permet d'éviter de redimensionner le buffer interne à chaque fois que de nouvelles données sont ajoutées, ce qui peut accélérer la génération du string.

Il est important de noter que la vitesse de génération d'un string en Java dépend de nombreux facteurs, dont la taille et la complexité du string, ainsi que de la performance de votre ordinateur. Vous devrez peut-être essayer plusieurs approches pour trouver celle qui convient le mieux à votre cas d'utilisation.
     */
    public String generateString(List<Vector3f> positions) {

        StringBuilder obj = new StringBuilder("# Exported from Java\n");
        String.format("#v (vertices) : %d\n", vertex.length);
        String.format("#vt (texture coords) : %d\n", texturesCoords.length);
        String.format("#vn (normals) : %d\n", normals.length);
        String.format("#f (faces) : %d\n", faces.length);

        obj.append("mtllib ").append("chunk.mtl").append("\n");
        obj.append("o ").append("chunk").append("\n");

        AtomicBoolean verticesExported = new AtomicBoolean(false);
        AtomicBoolean texturesCoordsExported = new AtomicBoolean(false);
        AtomicBoolean normalsExported = new AtomicBoolean(false);
        AtomicBoolean facesExported = new AtomicBoolean(false);

        StringBuilder vertexStringBuilder = new StringBuilder();
        StringBuilder texturesCoordsStringBuilder = new StringBuilder();
        StringBuilder normalsStringBuilder = new StringBuilder();
        StringBuilder facesStringBuilder = new StringBuilder();
        AtomicReference<String>[] vertexString = new AtomicReference[]{new AtomicReference<>("")};
        AtomicReference<String> texturesCoordsString = new AtomicReference<>("");
        AtomicReference<String> normalsString = new AtomicReference<>("");
        AtomicReference<String> facesString = new AtomicReference<>("");

        new Thread(() -> {
            for (Vector3f position : positions) {
                moveVertices(position);

                for (int i = 0; i < vertex.length; i += 3) {
                    vertexStringBuilder.append("v ").append(vertex[i]).append(" ").append(vertex[i + 1]).append(" ").append(vertex[i + 2]).append("\n");
                    //vertexString[0].set(vertexString[0].get() + "v " + vertex[i] + " " + vertex[i + 1] + " " + vertex[i + 2] + "\n");
                }
                vertexStringBuilder.append("\n");
                //vertexString[0].set(vertexString[0].get() + "\n");
            }

            verticesExported.set(true);
        }).start();
        new Thread(() -> {
            for (int i = 0; i < texturesCoords.length; i += 2) {
                texturesCoordsStringBuilder.append("vt ").append(texturesCoords[i]).append(" ").append(texturesCoords[i + 1]).append("\n");
                //texturesCoordsString.set(texturesCoordsString.get() + "vt " + texturesCoords[i] + " " + texturesCoords[i + 1] + "\n");
            }
            texturesCoordsStringBuilder.append("\n");
            //texturesCoordsString.set(texturesCoordsString.get() + "\n");
            texturesCoordsExported.set(true);
        }).start();
        new Thread(() -> {
            for (int i = 0; i < normals.length; i += 3) {
                normalsStringBuilder.append("vn ").append(normals[i]).append(" ").append(normals[i + 1]).append(" ").append(normals[i + 2]).append("\n");
                //normalsString.set(normalsString.get() + "vn " + normals[i] + " " + normals[i + 1] + " " + normals[i + 2] + "\n");
            }
            normalsStringBuilder.append("\n");
            //normalsString.set(normalsString.get() + "\n");
            normalsExported.set(true);
        }).start();

        new Thread(() -> {
            int offset = 1;
            int offsetFaces = 1;
            for (Vector3f position : positions) {
                for (int i = 0; i < faces.length; i += 3) {
                    if (i % 12 == 0) {
                        facesStringBuilder.append("\ns ").append(offsetFaces).append("\nf ");
                        //facesString.set(facesString.get() + "\ns " + offsetFaces);
                        //facesString.set(facesString.get() + "\nf ");
                        offsetFaces++;
                    }
                    facesStringBuilder.append(calculateFace(offset, faces[i])).append("/").append(faces[i + 1]).append("/").append(faces[i + 2]).append(" ");
                    //facesString.set(facesString.get() + (calculateFace(offset, faces[i])) + "/" + faces[i + 1] + "/" + faces[i + 2] + " ");
                }
                offset++;
            }
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
            obj.append(vertexStringBuilder);
            obj.append(texturesCoordsStringBuilder);
            obj.append(normalsStringBuilder);
            obj.append("\nusemtl Material\ns off\n");
            obj.append(facesStringBuilder);
        } else {
            Logger.getGlobal().warning("Exporting failed");
        }

        return obj.toString();
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
