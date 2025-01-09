package nl.saxion.Models.utils;

import nl.saxion.Models.Print;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.Spool;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public class Reader implements FileReader {

    public ArrayList<Print> readPrintsFromFile(String filePath) {

        return null;
    }

    public ArrayList<Printer> readPrintersFromFile(String filePath) {

        return null;
    }

    public ArrayList<Spool> readSpoolsFromFile(String filePath) {

        return null;
    }

    @Override
    public JSONArray readFile(String filePath) throws IOException {
        return null;
    }
}