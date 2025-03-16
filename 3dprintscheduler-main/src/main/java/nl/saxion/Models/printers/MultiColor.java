package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;
import java.util.List;

/* Printer capable of printing multiple colors. */
public class MultiColor extends Printer {
    private final int maxColors;
    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(int id, String printerName, String manufacturer, boolean isHoused, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, isHoused, maxX, maxY, maxZ);
        this.maxColors = maxColors;
    }

    @Override
    public List<Spool> getCurrentSpools() {
        List<Spool> spools = new ArrayList<>();
        spools.add(getCurrentSpool());
        if(spool2 != null) spools.add(spool2);
        if(spool3 != null) spools.add(spool3);
        if(spool4 != null) spools.add(spool4);
        return spools;
    }
    public int getMaxColors() {
        return maxColors;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        setCurrentSpool(spools.get(0));
        if(spools.size() > 1) spool2 = spools.get(1);
        if(spools.size() > 2) spool3 = spools.get(2);
        if(spools.size() > 3) spool4 = spools.get(3);
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
