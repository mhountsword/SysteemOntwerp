package nl.saxion.newModels;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;

public abstract class Printer {
    private int id;

    private String name;
    private String manufacturer;
    final int maxX;
    final int maxY;
    final int maxZ;
    Spool currentSpool;

    public Printer(int id, String name, String manufacturer, int maxX, int maxY, int maxZ, Spool currentSpool) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.currentSpool = currentSpool;
    }
    public Printer(String[] values){

    }
}
