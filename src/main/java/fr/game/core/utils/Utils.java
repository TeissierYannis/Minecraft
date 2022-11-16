package fr.game.core.utils;

import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> result = new ArrayList<>();
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
        }
        return result;
    }

    public static void writeLines(String path, ArrayList<String> lines) {
        File file = new File(path);

        try {
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        } else {
            System.out.println("File already exists.");
        }

        FileWriter myWriter = new FileWriter(path);
        for (String line : lines) {
            myWriter.write(line + "\r");
        }
        myWriter.close();

        } catch (Exception e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
        }
    }
}
