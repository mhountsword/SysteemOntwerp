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
    public Printer(String[] values){
        this.id = Integer.parseInt(values[0]);
        this.name = values[2];
        this.manufacturer = values[3];
        this.maxX = Integer.parseInt(values[4]);
        this.maxY = Integer.parseInt(values[5]);
        this.maxZ = Integer.parseInt(values[6]);

    }
}
