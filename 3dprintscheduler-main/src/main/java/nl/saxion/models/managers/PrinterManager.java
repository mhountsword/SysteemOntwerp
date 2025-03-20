package nl.saxion.models.managers;
import nl.saxion.models.printers.Printer;
import nl.saxion.utils.PrinterFactory;
import java.util.*;

public class PrinterManager {
    private static PrinterManager instance;
    private final List<Printer> printers = new ArrayList<>();
    private final List<Printer> freePrinters = new ArrayList<>();
    private final List<Printer> printingPrinters = new ArrayList<>();

    public List<Printer> getPrinters() {
       return printers;
    }
    public List<Printer> getFreePrinters() {
        return freePrinters;
    }
    public List<Printer> getPrintingPrinters() {
        return printingPrinters;
    }

    public void addPrinter(Printer printer){
        printers.add(printer);
        freePrinters.add(printer);
    }

    public void addFreePrinter(Printer printer){
        freePrinters.add(printer);
        printingPrinters.remove(printer);
    }
    public void addPrintingPrinter(Printer printer){
        printingPrinters.add(printer);
    }
    public void removeFreePrinter(Printer printer){
        freePrinters.remove(printer);
    }

    public void printPrinters(){
        System.out.println("--------- Available printers ---------");
        printers.forEach(printer -> System.out.println(printer.toString()));
        System.out.println("--------------------------------------");
    }

    public void printBusyPrinters() {
        System.out.println("--------- Printing printers ---------");
        printingPrinters.forEach(printer -> System.out.println(printer.toString()));
        System.out.println("--------------------------------------");
    }

    public static PrinterManager getInstance() {
        if (instance == null) {
            instance = new PrinterManager();
        }
        return instance;
    }
}

