package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    private boolean housed;
    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, boolean housed) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.housed = housed;

    }
    public StandardFDM(String[] values) {
        super(values);
        this.housed = values[1].equals("2");
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        setCurrentSpool(spools.get(0));
    }
    @Override
    public Spool[] getCurrentSpools() {
        return new Spool[0];
    }

    public boolean isHoused() {
        return housed;
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
        if (getCurrentSpool() != null) {
            append += "- Spool(s): " + getCurrentSpool().getId() + System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}
