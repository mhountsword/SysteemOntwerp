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

    public List<Print> getPrints() {
        return new ArrayList<>(prints);
    }
    public List<Printer> getPrinters() {
        return new ArrayList<>(printers);
    }
    public List<Spool> getSpools() {
        return spools;
    }
    public List<Printer> getFreePrinters() {
        return new ArrayList<>(freePrinters);
    }
    public List<Spool> getFreeSpools() {
        return new ArrayList<>(freeSpools);
    }

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

    public static PrinterManager getInstance() { //Singleton
        if (instance == null) {
            instance = new PrinterManager();
        }
        return instance;
    }

    public void printPrinters(){
        System.out.println("--------- Available printers ---------");
        printers.forEach(printer -> System.out.println(printer.toString()));
        System.out.println("--------------------------------------");
    }

    public void printSpools(){
        System.out.println("--------- Spools ---------");
        spools.forEach(spool -> System.out.println(spool.toString()));
        System.out.println("--------------------------------------");
    }
    public void printPrints(){
        System.out.println("---------- Available prints ----------");
        prints.forEach(print -> System.out.println(print.toString()));
        System.out.println("--------------------------------------");
    }
}

