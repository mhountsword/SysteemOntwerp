package nl.saxion.utils;

import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.spools.Spool;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Reader {
    private static final PrinterFactory printerFactory = new PrinterFactory();

    public ArrayList<Print> readPrintsFromFile(String filePath) {
        JSONParser jsonParser = new JSONParser();
        URL printResource = getClass().getResource("/" + filePath);

        if (printResource == null) {
            System.err.println("Warning: Could not find file " + filePath);
            return null;
        }

        ArrayList<Print> prints = new ArrayList<>();

        try (java.io.FileReader reader = new java.io.FileReader(URLDecoder.decode(printResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray allPrints = (JSONArray) jsonParser.parse(reader);
            for (Object p : allPrints) {
                Print newPrint = parsePrint((JSONObject) p);
                prints.add(newPrint);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return prints;
    }

    public ArrayList<Printer> readPrintersFromFile(String filePath) {
        JSONParser jsonParser = new JSONParser();
        URL printersResource = getClass().getResource("/" + filePath);

        if (printersResource == null) {
            System.err.println("Warning: Could not find file " + filePath);
            return null;
        }

        ArrayList<Printer> printers = new ArrayList<>();

        try (java.io.FileReader reader = new java.io.FileReader(URLDecoder.decode(printersResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray allPrinters = (JSONArray) jsonParser.parse(reader);
            for (Object p : allPrinters) {
                Printer newPrinter = parsePrinter((JSONObject) p);
                printers.add(newPrinter);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return printers;
    }
    
    public ArrayList<Spool> readSpoolsFromFile(String filePath) {
        JSONParser jsonParser = new JSONParser();
        URL spoolsResource = getClass().getResource("/" + filePath);

        if (spoolsResource == null) {
            System.err.println("Warning: Could not find file " + filePath);
            return null;
        }

        ArrayList<Spool> spools = new ArrayList<>();

        try (java.io.FileReader reader = new java.io.FileReader(URLDecoder.decode(spoolsResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray allSpools = (JSONArray) jsonParser.parse(reader);
            for (Object spoolObject : allSpools) {
                spools.add(parseSpool((JSONObject) spoolObject));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return spools;
    }

    private Printer parsePrinter(JSONObject printerJson) {
        // Parse common printer fields.
        int id = parseInt(printerJson.get("id"));
        String name = (String) printerJson.get("name");
        String manufacturer = (String) printerJson.get("manufacturer");
        int maxX = parseInt(printerJson.get("maxX"));
        int maxY = parseInt(printerJson.get("maxY"));
        int maxZ = parseInt(printerJson.get("maxZ"));
        int maxColors = parseInt(printerJson.get("maxColors"));

        // Delegate printer creation based on type
        int type = parseInt(printerJson.get("type"));
        return printerFactory.createPrinterByType(type, id, name, manufacturer, maxX, maxY, maxZ, maxColors);
    }

    private Print parsePrint(JSONObject p) {
        String name = (String) p.get("name");
        int height = ((Long) p.get("height")).intValue();
        int width = ((Long) p.get("width")).intValue();
        int length = ((Long) p.get("length")).intValue();
        JSONArray fLength = (JSONArray) p.get("filamentLength");
        int printTime = ((Long) p.get("printTime")).intValue();
        ArrayList<Double> filamentLength = new ArrayList<>();
        for (Object o : fLength) {
            filamentLength.add(((Double) o));
        }

        return new Print(name, height, width, length, filamentLength, printTime);
    }

    /**
     * Parses a JSONObject into a Spool instance.
     *
     * @param spoolJson The JSONObject representing a Spool.
     * @return A Spool object.
     * @throws IllegalArgumentException If the filamentType is invalid.
     */
    private Spool parseSpool(JSONObject spoolJson) {
        int id = ((Long) spoolJson.get("id")).intValue();
        String color = (String) spoolJson.get("color");
        String filamentType = (String) spoolJson.get("filamentType");
        double length = (Double) spoolJson.get("length");

        FilamentType type = FilamentType.fromTypeString(filamentType);
        return new Spool(id, color, type, length);
    }

    private int parseInt(Object value) {
        return ((Long) value).intValue();
    }
}