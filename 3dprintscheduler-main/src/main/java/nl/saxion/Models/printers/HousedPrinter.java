package nl.saxion.Models.printers;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;

public class HousedPrinter extends Printer{


    public HousedPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
    }

    @Override
    public int CalculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public Spool[] getCurrentSpools() {
        return new Spool[]{getCurrentSpool()};
    }

    @Override
    public void setCurrentSpools(ArrayList<Spool> spools) {

    }

    @Override
    public boolean printFits(Print print) {
        return print.getHeight() <= getMaxZ() && print.getWidth() <= getMaxX() && print.getLength() <= getMaxY();
    }
}
