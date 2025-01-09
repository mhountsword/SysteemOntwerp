package nl.saxion.Models.printers;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;

import java.util.ArrayList;

public abstract class Printer {
    private final int id;
    private final String name;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public Printer(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        this.id = id;
        this.name = printerName;
        this.manufacturer = manufacturer;
        this.maxY = maxY;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }


    public abstract int CalculatePrintTime(String filename);

    public abstract Spool[] getCurrentSpools();

    public abstract void setCurrentSpools(ArrayList<Spool> spools);

    public abstract boolean printFits(Print print);

    @Override
    public String toString() {
        return  "--------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator() +
                "--------";
    }

    public String getName(){
        return name;
    }
    public String getManufacturer(){
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
