package nl.saxion;

import nl.saxion.models.managers.*;
import nl.saxion.models.observer.Observer;
import nl.saxion.models.observer.Updater;
import nl.saxion.models.printers.*;
import nl.saxion.models.prints.Print;
import nl.saxion.models.spools.Spool;
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
        printers.forEach(printerManager::addPrinter);
    }

    public void changePrintStrategy() {
        strategyManager.changePrintStrategy();
    }

    public void registerPrintCompletion(String completionMessage) {
        if(printTaskManager.getRunningPrintTasks().isEmpty()) {
            System.out.println("No running print tasks. please start a print queue first");
        } else {
            System.out.println("---------- Register Print " + completionMessage + " ----------");
            printerManager.printBusyPrinters();
            System.out.print("- Printer ID: ");
            int printerId = scanner.nextInt();
            try {
                printTaskManager.registerPrintCompletion(printerId);
            } catch (PrintError e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void addNewPrintTask() {
        printTaskManager.addNewPrintTask();
    }

    public void startPrintQueue() {
        if (printTaskManager.getPendingPrintTasks().isEmpty()) {
            System.out.println("No pending print tasks. please add a new print task first");
        } else {
            System.out.println("---------- Starting Print Queue ----------");
            printTaskManager.startQueue();
            System.out.println("-----------------------------------");
        }
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

    public int getSpoolChanges() {
        return spoolchanges;
    }

    public int getTotalPrints() {
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

    public void showStats() {
        System.out.println("---------- Statistics ----------");
        System.out.println("Spool changes: " + getSpoolChanges());
        System.out.println("total prints: " + getTotalPrints());
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
