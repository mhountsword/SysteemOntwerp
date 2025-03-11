package nl.saxion;

import nl.saxion.Models.managers.PrintManager;
import nl.saxion.Models.managers.PrintTaskManager;
import nl.saxion.Models.managers.PrinterManager;
import nl.saxion.Models.managers.SpoolManager;
import nl.saxion.Models.printers.*;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.Spool;
import nl.saxion.utils.Reader;
import nl.saxion.exceptions.PrintError;

import java.util.*;
import java.util.stream.IntStream;

public class Facade {
//    TODO: er zit te veel business logic in de facade.
    private final PrinterManager printerManager;
    private final PrintManager printManager;
    private final PrintTaskManager printTaskManager;
    private final SpoolManager spoolManager;
    private String printStrategy = "Less Spool Changes";
    private final Scanner scanner = new Scanner(System.in);
    private static Facade instance;

    private int getMaxColors(Printer printer) {
        return (printer instanceof MultiColor) ? ((MultiColor) printer).getMaxColors() : 1;
    }



    private Facade() {
        printerManager = PrinterManager.getInstance();
        printManager = PrintManager.getInstance();
        printTaskManager = PrintTaskManager.getInstance();
        spoolManager = SpoolManager.getInstance();



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
                getMaxColors(printer)
        ));
    }

    public void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " +getCurrentStrategy());
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.print("- Choose strategy: ");
        int strategyChoice = numberInput(1, 2);
        System.out.println("-----------------------------------");
        if (strategyChoice == 1) {
            printStrategy = "Less Spool Changes";
        } else if (strategyChoice == 2) {
            printStrategy = "Efficient Spool Usage";
        }
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
        try {
            System.out.println("---------- New Print Task ----------");
            List<String> colors = new ArrayList<>();
            List<Print> prints = printManager.getPrints();
            if (prints.isEmpty()) {
                System.out.println("no available prints");
                return;
            }
            IntStream.range(0, prints.size())
                    .forEach(i -> System.out.println("- " + (i + 1) + ": " + prints.get(i).getName()));

            System.out.print("- Print number: ");
            int printNumber = numberInput(1, prints.size());

            Print print = prints.get(printNumber - 1);

            System.out.println("---------- Filament Type ----------");
            System.out.println("- 1: PLA");
            System.out.println("- 2: PETG");
            System.out.println("- 3: ABS");
            System.out.print("- Filament type number: ");
            int filamentType = numberInput(1, 3);
            FilamentType type = switch (filamentType) {
                case 2 -> FilamentType.PETG;
                case 3 -> FilamentType.ABS;
                default -> FilamentType.PLA;
            };
            addPrintTask(print.getName(), colors, type);
            System.out.println("----------------------------");
        } catch (PrintError e) {
            System.out.println(e.getMessage());
        }
    }



    public void startPrintQueue() {
        System.out.println("---------- Starting Print Queue ----------");
        printTaskManager.startQueue();
        System.out.println("-----------------------------------");

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

    public PrintTask getPrinterCurrentTask(Printer printer) {
        return printTaskManager.getPrinterCurrentTask(printer);
    }

    public void showPendingPrintTasks() {
        printTaskManager.printPendingPrintTasks();
    }

    public String getCurrentStrategy() {
        return printStrategy;
    }

    public int numberInput(int min, int max) {
        int input = scanner.nextInt();
        while (input < min || input > max) {
            input = scanner.nextInt();
        }
        return input;
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
