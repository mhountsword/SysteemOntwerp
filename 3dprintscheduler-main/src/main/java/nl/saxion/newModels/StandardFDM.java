package nl.saxion.Models;
import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    private boolean housed;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private Spool currentSpool;

    public StandardFDM(String[] values) {
        super(Integer.parseInt(values[0]), values[2], values[4]);
        this.maxX = Integer.parseInt(values[5]);
        this.maxY = Integer.parseInt(values[6]);
        this.maxZ = Integer.parseInt(values[7]);
        if (values[1].equals("1")){
            this.housed = false;
        }else this.housed = true;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        this.currentSpool = spools.get(0);
    }

    public void setCurrentSpool(Spool spool) {
        this.currentSpool = spool;
    }

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[1];
        if(currentSpool != null) {
            spools[0] = currentSpool;
        }
        return spools;
    }

    @Override
    public boolean printFits(Print print) {
        return print.getHeight() <= maxZ && print.getWidth() <= maxX && print.getLength() <= maxY;
    }

    @Override
    public int CalculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "- maxX: " + maxX + System.lineSeparator() +
                "- maxY: " + maxY + System.lineSeparator() +
                "- maxZ: " + maxZ + System.lineSeparator();
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId()+ System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}
