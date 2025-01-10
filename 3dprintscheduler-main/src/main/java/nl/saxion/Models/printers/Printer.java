package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;

public abstract class Printer {
    private final int id;
    private final String name;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
//    "id": 1,
//    "type": 1,
//    "name": "Enterprise",
//    "model": "Ender 3",
//    "manufacturer": "Creality",
//    "maxX": 220,
//    "maxY": 220,
//    "maxZ": 250,
//    "maxColors": 1

    public Printer(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        this.id = id;
        this.name = printerName;
        this.manufacturer = manufacturer;
        this.maxY = maxY;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    public Printer(String[] values) {
        this.id = Integer.parseInt(values[0]);
        this.name = values[2];
        this.manufacturer = values[4];
        this.maxX = Integer.parseInt(values[5]);
        this.maxY = Integer.parseInt(values[6]);
        this.maxZ = Integer.parseInt(values[7]);
    }

    public abstract int CalculatePrintTime(String filename);

    public abstract Spool[] getCurrentSpools();

    public abstract void setCurrentSpools(ArrayList<Spool> spools);

    public abstract boolean printFits(Print print);

    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator() +
                "--------";
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getId() {
        return id;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }
}
