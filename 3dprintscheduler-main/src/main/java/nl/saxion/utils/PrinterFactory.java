package nl.saxion.utils;

import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;
import nl.saxion.exceptions.BadPrinterType;

public class PrinterFactory {

    public Printer createPrinterByType(int id, int type, String name, String manufacturer, boolean isHoused, int maxX, int maxY, int maxZ, int maxColors) {
        return switch (getPrinterType(type, maxColors)) {
            case "StandardFDM" -> new StandardFDM(id, name, manufacturer, isHoused, maxX, maxY, maxZ);
            case "MultiColor" -> new MultiColor(id, name, manufacturer, isHoused, maxX, maxY, maxZ, maxColors);
            default -> throw new BadPrinterType("Printer type not recognized.");
        };
    }

    private String getPrinterType(int type, int maxColors) {
        if(type == 1 || type == 2) {
            return "StandardFDM";
        } else if (maxColors > 1) {
            return "MultiColor";
        } else {
            System.err.println("Printer type of type " + type + " not recognized.");
            return "Unknown";
        }
    }
}
