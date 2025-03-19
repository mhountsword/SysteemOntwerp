package nl.saxion.Models.managers;
import nl.saxion.Models.printers.Printer;
import nl.saxion.utils.PrinterFactory;
import java.util.*;

public class PrinterManager {
    private static PrinterManager instance;
    private final List<Printer> printers = new ArrayList<>();
    private final List<Printer> freePrinters = new ArrayList<>();

    public List<Printer> getPrinters() {
       return printers;
    }
    public List<Printer> getFreePrinters() {
        return freePrinters;
    }

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, boolean isHoused, int maxX, int maxY, int maxZ, int maxColors) { //receive String[]
        Printer newPrinter = new PrinterFactory().createPrinterByType(id, printerType, printerName, manufacturer, isHoused, maxX, maxY, maxZ, maxColors);
        printers.add(newPrinter);
        freePrinters.add(newPrinter);
    }

    public void addPrinter(Printer printer){
        printers.add(printer);
        freePrinters.add(printer);
    }


    public void addfreeprinter(Printer printer){
        freePrinters.add(printer);
    }
    public void removefreeprinter(Printer printer){
        freePrinters.remove(printer);
    }

    public void printPrinters(){
        System.out.println("--------- Available printers ---------");
        printers.forEach(printer -> System.out.println(printer.toString()));
        System.out.println("--------------------------------------");
    }

    public static PrinterManager getInstance() {
        if (instance == null) {
            instance = new PrinterManager();
        }
        return instance;
    }
}

