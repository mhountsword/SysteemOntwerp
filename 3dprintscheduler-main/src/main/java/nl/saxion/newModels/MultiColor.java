package nl.saxion.Models;

import java.util.ArrayList;

/* Printer capable of printing multiple colors. */
public class MultiColor extends Printer {
    private int maxColors;
    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(String[] values) {
        super(Integer.parseInt(values[0]), values[2], values[4]);
        this.maxColors = Integer.parseInt(values[8]);

    }

    @Override
    public int CalculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public Spool[] getCurrentSpools() {
        return new Spool[0];
    }

    @Override
    public void setCurrentSpools(ArrayList<Spool> spools) {

    }

    @Override
    public boolean printFits(Print print) {
        return false;
    }
}