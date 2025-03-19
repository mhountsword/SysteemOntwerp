package nl.saxion;

import nl.saxion.Models.managers.*;
import nl.saxion.Models.observer.Observer;
import nl.saxion.Models.observer.Updater;
import nl.saxion.Models.printers.*;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.Spool;
import nl.saxion.exceptions.PrintError;
import nl.saxion.utils.readers.Reader;

import java.util.*;

public class Facade implements Updater {
    private int spoolchanges = 0;
    private int totalprints = 0;
    private final PrinterManager printerManager;
    private final PrintManager printManager;
    private final PrintTaskManager printTaskManager;
    private final SpoolManager spoolManager;
    private final StrategyManager strategyManager;
    private final Scanner scanner = new Scanner(System.in);
    private static Facade instance;

    private final Observer observer;

    private static final int TYPE_UNKNOWN = 0;
    private static final int TYPE_STANDARD_FDM = 1;
    private static final int TYPE_HOUSED_MULTICOLOR = 2;
    private static final int TYPE_MULTICOLOR = 3;
    private static final int TYPE_HOUSED = 4;


    private Facade() {
        printerManager = PrinterManager.getInstance();
        printManager = PrintManager.getInstance();
        printTaskManager = PrintTaskManager.getInstance();
        spoolManager = SpoolManager.getInstance();
        strategyManager = StrategyManager.getInstance();
        observer = Observer.getInstance();
        observer.subscribe(this);


        Reader fileReader = new Reader();
        List<Print> prints = fileReader.readPrintsFromFile("prints.json");
        List<Spool> spools = fileReader.readSpoolsFromFile("spools.json");
        List<Printer> printers = fileReader.readPrintersFromFile("printers.json");

        spools.forEach(spoolManager::addSpool);
        prints.forEach(printManager::addPrint);
        for (Printer printer : printers) {
            printerManager.addPrinter(
                    printer.getId(),
                    calculatePrinterTypeCategory(printer),
                    printer.getName(),
                    printer.getManufacturer(),
                    printer.isHoused(),
                    printer.getMaxX(),
                    printer.getMaxY(),
                    printer.getMaxZ(),
                    printer.getMaxColors(printer));
        }
    }

    public void changePrintStrategy() {
        strategyManager.changePrintStrategy();
    }

    public void registerPrintCompletion() {
        System.out.println("---------- Register Print Completion ----------");
        System.out.print("- Printer ID: ");
        System.out.println("-------------------------------------------");
        int printerId = scanner.nextInt();
        try {
            printTaskManager.registerPrintCompletion(printerId);
        } catch (PrintError e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNewPrintTask() {
        printTaskManager.addNewPrintTask();
    }

    public void startPrintQueue() {
        System.out.println("---------- Starting Print Queue ----------");
        printTaskManager.startQueue();
        System.out.println("-----------------------------------");
    }

    public void showPendingPrintTasks() {
        printTaskManager.printPendingPrintTasks();
    }

    public void showPrinters() {
        printerManager.printPrinters();
    }

    public void showPrints() {
        printManager.printPrints();
    }

    public void showSpools() {
        spoolManager.printSpools();
    }

    public int getSpoolchanges() {
        return spoolchanges;
    }

    public int getTotalprints() {
        return totalprints;
    }

    private int calculatePrinterTypeCategory(Printer printer) {
        if (printer instanceof StandardFDM) {
            return TYPE_STANDARD_FDM;
        }
        boolean isMultiColor = printer instanceof MultiColor;
        boolean isHoused = printer.isHoused();
        if (isHoused && isMultiColor) {
            return TYPE_HOUSED_MULTICOLOR;
        }
        if (isMultiColor) {
            return TYPE_MULTICOLOR;
        }
        if (isHoused) {
            return TYPE_HOUSED;
        }
        return TYPE_UNKNOWN;
    }

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    public void showstats() {
        System.out.println("---------- Statistics ----------");
        System.out.println("Spool changes: " + getSpoolchanges());
        System.out.println("total prints: " + getTotalprints());
        System.out.println("-------------------------------------------");

    }

    public void exit(){
        observer.unsubscribe(instance);
        System.out.println("goodbye");
    }

    @Override
    public void update(int spoolchanges, int totalprints) {
        this.spoolchanges = spoolchanges;
        this.totalprints = totalprints;
    }
}
