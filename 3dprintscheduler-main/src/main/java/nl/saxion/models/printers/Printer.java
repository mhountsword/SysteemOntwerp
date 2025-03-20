package nl.saxion.models.printers;

import nl.saxion.models.prints.Print;
import nl.saxion.models.prints.PrintTask;
import nl.saxion.models.spools.FilamentType;
import nl.saxion.models.spools.Spool;

import java.util.List;

public abstract class Printer {
    private final int id;
    private final String name;
    private final String manufacturer;
    private final boolean isHoused;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private boolean isPrinting = false;
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

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public abstract List<Spool> getCurrentSpools();

    public abstract void setCurrentSpools(List<Spool> spools);

    public void setCurrentSpool(Spool currentSpool) {
        this.currentSpool = currentSpool;
    }

    public boolean printFits(Print print){
        return print.getHeight() > getMaxZ() || print.getWidth() > getMaxX() || print.getLength() > getMaxY();
    }

    public boolean taskCompatible(PrintTask task) {
        if (task.getFilamentType() == FilamentType.ABS) {
            if (!isHoused) {
                return false;
            }
        }
        if (!printFits(task.getPrint())) {
            return false;
        }
        if (task.getColors().size() > getMaxColors(this)) {
            return false;
        }
        return true;
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

    public boolean isPrinting() {
        return isPrinting;
    }

    public void setPrinting(boolean isPrinting) {
        this.isPrinting = isPrinting;
    }

    public boolean isHoused() {return isHoused;}
}
