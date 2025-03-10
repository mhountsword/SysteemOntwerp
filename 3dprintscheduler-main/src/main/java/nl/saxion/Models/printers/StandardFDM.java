package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        setCurrentSpool(spools.get(0));
    }
    @Override
    public Spool[] getCurrentSpools() {
        Spool[] result = new Spool[1];
        result[0] = getCurrentSpool();
        return result;
    }

    @Override
    public boolean printFits(Print print) {
        return print.height() <= getMaxZ() && print.width() <= getMaxX() && print.length() <= getMaxY();
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
