package nl.saxion.models.printers;

import nl.saxion.models.spools.Spool;

import java.util.ArrayList;
import java.util.List;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    public StandardFDM(int id, String printerName, String manufacturer, boolean isHoused, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, isHoused, maxX, maxY, maxZ);
    }

    @Override
    public List<Spool> getCurrentSpools() {
        List<Spool> spools = new ArrayList<>();
        spools.add(getCurrentSpool());
        return spools;
    }

    @Override
    public void setCurrentSpools(List<Spool> spools) {
        this.setCurrentSpool(spools.getFirst());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String printerDimensions = "- maxX: " + getMaxX() + System.lineSeparator() +
                "- maxY: " + getMaxY() + System.lineSeparator() +
                "- maxZ: " + getMaxZ() + System.lineSeparator();
        if (getCurrentSpool() != null) {
            printerDimensions += "- Spool(s): " + getCurrentSpool().getId() + System.lineSeparator();
        }
        sb.append(super.toString());
        sb.append(printerDimensions);

        return sb.toString();
    }
}
