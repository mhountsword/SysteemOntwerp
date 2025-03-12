package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;
import java.util.List;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
    }

    @Override
    public List<Spool> getCurrentSpools() {
        List<Spool> spools = new ArrayList<>();
        spools.add(getCurrentSpool());
        return spools;
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
