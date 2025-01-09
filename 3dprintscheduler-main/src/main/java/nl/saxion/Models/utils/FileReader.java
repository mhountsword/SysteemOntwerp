package nl.saxion.Models.utils;

import org.json.simple.JSONArray;
import java.io.IOException;

public interface FileReader {
    /**
     * Reads the file and converts it into a JSON Array.
     *
     * @param filePath Path to the file.
     * @return A JSON representation of the data.
     * @throws IOException if the file can't be read.
     */
    JSONArray readFile(String filePath) throws IOException;
}