package nl.saxion;

import nl.saxion.Models.managers.*;
import nl.saxion.Models.printers.*;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.Spool;
import nl.saxion.exceptions.PrintError;
import nl.saxion.utils.readers.Reader;
import java.util.*;

public class Facade {
//    TODO: er zit te veel business logic in de facade.
    private final PrinterManager printerManager;
    private final PrintManager printManager;
    private final PrintTaskManager printTaskManager;
    private final SpoolManager spoolManager;
    private final StrategyManager strategyManager;
    private String printStrategy = "Less Spool Changes";
    private final Scanner scanner = new Scanner(System.in);
    private static Facade instance;

    private Facade() {
        printerManager = PrinterManager.getInstance();
        printManager = PrintManager.getInstance();
        printTaskManager = PrintTaskManager.getInstance();
        spoolManager = SpoolManager.getInstance();
        strategyManager = StrategyManager.getInstance();

        Reader fileReader = new Reader();
        List<Print> prints = fileReader.readPrintsFromFile("prints.json");
        List<Spool> spools = fileReader.readSpoolsFromFile("spools.json");
        List<Printer> printers = fileReader.readPrintersFromFile("printers.json");

        spools.forEach(spoolManager::addSpool);
        prints.forEach(printManager::addPrint);

        printers.forEach(printer -> printerManager.addPrinter(
                printer.getId(),
                getPrinterType(printer),
                printer.getName(),
                printer.getManufacturer(),
                printer.getMaxX(),
                printer.getMaxY(),
                printer.getMaxZ(),
                printer.getMaxColors(printer)
        ));
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
        }catch (PrintError e){
            System.out.println(e.getMessage());
        }
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType filamentType) throws PrintError {
        printTaskManager.addPrintTask(printName,colors,filamentType);
    }
    public void addNewPrintTask() {
        // TODO: color is een lege arraylist. dit kan niet. vervang met een keuze menu.
        printTaskManager.addNewPrintTask();
    }

    public void startPrintQueue() {
        printTaskManager.startQueue();
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


    private int getPrinterType(Printer printer) {
        return switch (printer) {
            case HousedPrinter housedPrinter -> 2;
            case MultiColor multiColor -> 3;
            case StandardFDM standardFDM -> 1;
            default -> -1;
        };
    }

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }
}
