package nl.saxion.models.printers;

import nl.saxion.models.spools.Spool;

import java.util.ArrayList;
import java.util.List;

/* Printer capable of printing multiple colors. */
public class MultiColor extends Printer {
    private final int maxColors;
    private final ArrayList<Spool> spoolList = new ArrayList<>();

    public MultiColor(int id, String printerName, String manufacturer, boolean isHoused, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, isHoused, maxX, maxY, maxZ);
        this.maxColors = maxColors;
    }

    @Override
    public List<Spool> getCurrentSpools() {
        List<Spool> spools = new ArrayList<>();
        Spool currentSpool = getCurrentSpool();
        if(currentSpool != null) {
            spools.add(currentSpool);
        }

        for(Spool spool : spoolList) {
            if(spool != null) {
                spools.add(spool);
            }
        }
        return spools;
    }

    @Override
    public void setCurrentSpools(List<Spool> spools) {
        for(Spool spool : spools) {
            if(spool != null) {
                spoolList.add(spool);
            }
        }
    }

    public int getMaxColors() {
        return maxColors;
    }

    @Override
    public String toString() {
        List<Spool> currentSpools = this.getCurrentSpools();
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString());
        sb.append("Max Colors: ").append(maxColors).append("\n");
        if(!currentSpools.isEmpty() && currentSpools.getFirst() != null) {
            sb.append("Current Spools:\n");
            for (Spool spool : currentSpools) {
                sb.append(spool.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
