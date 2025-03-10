package nl.saxion.Models.managers;

import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;
import nl.saxion.Models.utils.PrinterFactory;

import java.util.*;

public class PrinterManager {
    private static PrinterManager instance;
    private final List<Printer> printers = new ArrayList<>();
    private final List<Printer> freePrinters = new ArrayList<>();
    private final List<Spool> spools = new ArrayList<>();
    private final List<Spool> freeSpools = new ArrayList<>();
    private final List<Print> prints = new ArrayList<>();


    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) { //receive String[]
        Printer newPrinter = new PrinterFactory().createPrinterByType(id, printerType, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
        printers.add(newPrinter);
        freePrinters.add(newPrinter);
    }

    public void addPrint(Print print) {
        Print p = new Print(print.getName(), print.getHeight(), print.getWidth(), print.getLength(), print.getFilamentLength(), print.getPrintTime());
        prints.add(p);
    }

    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }

    public void addSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public List<Print> getPrints() {
        return prints;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public List<Printer> getFreePrinters() {
        return freePrinters;
    }

    public List<Spool> getFreeSpools() {
        return freeSpools;
    }

    public static PrinterManager getInstance() { //Singleton
        if (instance == null) {
            instance = new PrinterManager();
        }
        return instance;
    }

    public List<Spool> getSpools() {
        return spools;
    }
}

