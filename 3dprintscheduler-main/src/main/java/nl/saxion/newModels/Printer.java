package nl.saxion.newModels;
import java.util.ArrayList;

public abstract class Printer {
     private final int id;
    private final String name;
    private final String manufacturer;
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

    public Printer(int id, String name, String manufacturer, int maxX, int maxY, int maxZ, Spool currentSpool) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.currentSpool = currentSpool;
    }

    public Printer(int id, String name, String manufacturer, int maxX, int maxY, int maxZ) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public abstract boolean printFits(Print print);

    public abstract int CalculatePrintTime(String filename);

    public abstract Spool[] getCurrentSpools();

    public abstract void setCurrentSpools(ArrayList<Spool> spools);


}
