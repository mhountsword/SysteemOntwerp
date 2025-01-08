package nl.saxion.newModels;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.saxion.Models.FilamentType;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class Reader {
    Scanner scanner;
    public ArrayList<Spool> readSpoolsFromFile(String filename) throws FileNotFoundException {
        ArrayList<Spool> result = new ArrayList<>();
        scanner = new Scanner(new File(filename));
        scanner.nextLine();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] lineparts = line.split(",");
            FilamentType filamentType = FilamentType.valueOf(lineparts[2]);
            Spool spool = new Spool(Integer.parseInt(lineparts[0]), lineparts[1], filamentType, Double.parseDouble(lineparts[3]));
            result.add(spool);
        }
        return result;
    }

    public ArrayList<Print> readPrintsFromFile(String filename) throws IOException {
        ArrayList<Print> result = new ArrayList<>();
        PrinterFactory factory = new PrinterFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(filename));
        for (JsonNode item : jsonNode) {
            ArrayList<Double> filamentlength = new ArrayList<>();
            for (JsonNode array : item.get("filamentLength")) {
                filamentlength.add(array.asDouble());
            }
            System.out.println();
            result.add(new Print(item.get("name").asText(), item.get("height").asInt(), item.get("width").asInt(), item.get("length").asInt(), filamentlength, item.get("printTime").asInt()));
        }
        return result;
    }

    public ArrayList<Printer> readPrintersFromFile(String filename) throws IOException, PrinterException {
        ArrayList<Printer> result = new ArrayList<>();
        PrinterFactory factory = new PrinterFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(filename));
        for (JsonNode item : jsonNode) {
            String[] parts = new String[9];
            parts[0] = item.get("id").asText();
            parts[1] = item.get("type").asText();
            parts[2] = item.get("name").asText();
            parts[3] = item.get("model").asText();
            parts[4] = item.get("manufacturer").asText();
            parts[5] = item.get("maxX").asText();
            parts[6] = item.get("maxY").asText();
            parts[7] = item.get("maxZ").asText();
            parts[8] = item.get("maxColors").asText();
            result.add(factory.addPrinter(parts));
        }
        return result;
    }
}

