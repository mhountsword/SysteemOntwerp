package nl.saxion.Models;

import java.util.ArrayList;

public abstract class OldPrinter {
    private int id;
    private String name;
    private String manufacturer;

    public OldPrinter(int id, String printerName, String manufacturer) {
        this.id = id;
        this.name = printerName;
        this.manufacturer = manufacturer;
    }

    public int getId() {
        return id;
    }

    public abstract int CalculatePrintTime(String filename);

    public abstract OldSpool[] getCurrentSpools();

    public abstract void setCurrentSpools(ArrayList<OldSpool> spools);

    public abstract boolean printFits(OldPrint print);

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
}
