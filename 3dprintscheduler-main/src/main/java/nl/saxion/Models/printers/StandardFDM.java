package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    private boolean housed;
    private Spool currentSpool;

    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, boolean housed) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.housed = housed;

    }
    public StandardFDM(String[] values) {
        super(values);
        this.housed = values[1].equals("2");
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        this.currentSpool = spools.get(0);
    }

    public void setCurrentSpool(Spool spool) {
        this.currentSpool = spool;
    }

    public boolean isHoused() {
        return housed;
    }

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[1];
        if (currentSpool != null) {
            spools[0] = currentSpool;
        }
        return spools;
    }

    @Override
    public boolean printFits(Print print) {
        return print.getHeight() <= getMaxZ() && print.getWidth() <= getMaxX() && print.getLength() <= getMaxY();
    }

    @Override
    public int CalculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "- maxX: " + getMaxX() + System.lineSeparator() +
                "- maxY: " + getMaxY() + System.lineSeparator() +
                "- maxZ: " + getMaxZ() + System.lineSeparator();
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId() + System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}
