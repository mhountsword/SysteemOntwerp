package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.List;

public class Printer {
    private final int id;
    private final String name;
    private final String manufacturer;
    private final boolean isHoused;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private Spool currentSpool;

    public Printer(int id, String printerName, String manufacturer, boolean isHoused, int maxX, int maxY, int maxZ) {
        this.id = id;
        this.name = printerName;
        this.manufacturer = manufacturer;
        this.isHoused = isHoused;
        this.maxY = maxY;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    public int CalculatePrintTime(String filename){
        return 0;
    }

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public void setCurrentSpool(Spool currentSpool) {
        this.currentSpool = currentSpool;
    }

    public List<Spool> getCurrentSpools(){
        return null;
    }

    public void setCurrentSpools(List<Spool> spools){
        this.currentSpool = spools.getFirst();
    }

    public boolean printFits(Print print){
        return print.getHeight() > getMaxZ() || print.getWidth() > getMaxX() || print.getLength() > getMaxY();
    }

    public int getMaxColors(Printer printer) {
        return (printer instanceof MultiColor) ? ((MultiColor) printer).getMaxColors() : 1;
    }

    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator();
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

    public boolean isHoused() {return isHoused;}
}
