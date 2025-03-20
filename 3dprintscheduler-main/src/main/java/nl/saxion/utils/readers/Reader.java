package nl.saxion.utils.readers;

import nl.saxion.models.spools.FilamentType;
import nl.saxion.models.prints.Print;
import nl.saxion.models.printers.Printer;
import nl.saxion.models.spools.Spool;
import nl.saxion.exceptions.BadFileExtension;
import nl.saxion.utils.PrinterFactory;
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
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
        }
        return printers;
    }
    
    public ArrayList<Spool> readSpoolsFromFile(String filePath) {
        if(filePath.toLowerCase().endsWith(".csv")) {
            return readSpoolsFromCsvFile(filePath);
        } else if (filePath.toLowerCase().endsWith(".json")) {
            return readSpoolsFromJsonFile(filePath);
        } else {
            throw new BadFileExtension(filePath + " is not a valid file");
        }
    }

    private ArrayList<Spool> readSpoolsFromJsonFile(String filePath) {
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
            System.err.println(e.getMessage());
        }
        return spools;
    }

    private ArrayList<Spool> readSpoolsFromCsvFile(String filePath) {
        CSVAdapter csvAdapter = new CSVAdapter();
        ArrayList<Spool> spools = new ArrayList<>();
        try {
            JSONArray jsonArray = csvAdapter.readFile(filePath);
            for (Object spoolObject : jsonArray) {
                spools.add(parseSpool((JSONObject) spoolObject));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
        return spools;
    }

    private Printer parsePrinter(JSONObject printerJson) {
        boolean isHoused;
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
        isHoused = type == 2 || type > 3;

        return printerFactory.createPrinterByType(id, type, name, manufacturer, isHoused, maxX, maxY, maxZ, maxColors);
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
        int id = Integer.parseInt(spoolJson.get("id").toString());
        String color = (String) spoolJson.get("color");
        String filamentTypeStr = (String) spoolJson.get("filamentType");
        double length = Double.parseDouble(spoolJson.get("length").toString());

        FilamentType filamentType = FilamentType.valueOf(filamentTypeStr.toUpperCase());
        return new Spool(id, color, filamentType, length);
    }

    private int parseInt(Object value) {
        return ((Long) value).intValue();
    }
}