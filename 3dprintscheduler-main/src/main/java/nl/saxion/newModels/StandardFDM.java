package nl.saxion.newModels;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    private boolean housed;
    private Spool currentSpool;


    public StandardFDM(String[] values) {
        super(values);
        if (values[1].equals("1")){
            this.housed = false;
        }else this.housed = false;
    }


    public void setCurrentSpools(ArrayList<Spool> spools) {
        this.currentSpool = spools.get(0);
    }


    public void setCurrentSpool(Spool spool) {
        this.currentSpool = spool;
    }

    public Spool getCurrentSpool() {
        return this.currentSpool;
    }


//TODO: waarom returnen we een array? Dat is alleen nodig bij multicolor?
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
