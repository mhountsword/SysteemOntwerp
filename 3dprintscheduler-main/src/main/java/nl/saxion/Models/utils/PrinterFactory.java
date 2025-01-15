package nl.saxion.Models.utils;

import nl.saxion.Models.printers.HousedPrinter;
import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;

public class PrinterFactory {

    public PrinterFactory() {
    }

    public Printer createPrinterByType(int type, int id, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        return switch (type) {
            case 1 -> new StandardFDM(id, name, manufacturer, maxX, maxY, maxZ, false);
            case 2 ->
                    new HousedPrinter(id, name, manufacturer, maxX, maxY, maxZ);// new StandardFDM(id, name, manufacturer, maxX, maxY, maxZ, true);

            default -> new MultiColor(id, name, manufacturer, maxX, maxY, maxZ, maxColors);
        };
    }
}
