package graphql4smr.lib.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class Util {
    public static String sendAsJson(Map<String, Object> toSpecificationResult) {
        Gson gson = new Gson();
        return gson.toJson(toSpecificationResult);
    }

    // For readable multiline string in java
    // copy this Line
    // public String include_str(String path){return Util.include_str(getClass(), path);}

    public static String include_str(Class<?> clazz, String fileName) {
        String fileContent = "";
        try (InputStream inputStream = clazz.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            while (scanner.hasNextLine()) {
                fileContent += scanner.nextLine() + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

}
