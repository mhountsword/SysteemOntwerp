package nl.saxion.newModels;
import java.util.ArrayList;
/* Printer capable of printing multiple colors. */
public class MultiColor extends Printer {
    private final int maxColors;
    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(String[] values) {
        super(values);
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
        return print.getHeight() <= maxZ && print.getWidth() <= maxX && print.getLength() <= maxY;
    }

    public int getMaxColors() {
        return maxColors;
    }
}