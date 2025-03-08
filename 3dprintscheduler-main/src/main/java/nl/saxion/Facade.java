package nl.saxion;

import nl.saxion.Models.managers.PrintTaskManager;
import nl.saxion.Models.managers.PrinterManager;
import nl.saxion.Models.printers.*;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.Spool;
import nl.saxion.Models.utils.Reader;
import nl.saxion.exceptions.PrintError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Facade {
//    TODO: er zit te veel business logic in de facade.
    private final PrinterManager printerManager = PrinterManager.getInstance();
    private final PrintTaskManager printTaskManager = new PrintTaskManager();
    private String printStrategy = "Less Spool Changes";


    private int getMaxColors(Printer printer) {
        return (printer instanceof MultiColor) ? ((MultiColor) printer).getMaxColors() : 1;
    }

    public void initialize() {
        Reader fileReader = new Reader();
        List<Print> prints = fileReader.readPrintsFromFile("prints.json");
        List<Spool> spools = fileReader.readSpoolsFromFile("spools.json");
        List<Printer> printers = fileReader.readPrintersFromFile("printers.json");

       spools.forEach(printerManager::addSpool);

        prints.forEach(printerManager::addPrint);

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

    public void changePrintStrategy(int strategyChoice) {
        if (strategyChoice == 1) {
            printStrategy = "Less Spool Changes";
        } else if (strategyChoice == 2) {
            printStrategy = "Efficient Spool Usage";
        }
    }

    public void registerPrintCompletion(int printerId) throws PrintError {
        Map.Entry<Printer, PrintTask> foundEntry = printTaskManager.getAllRunningTasks().entrySet().stream()
                .filter(entry -> entry.getKey().getId() == printerId)
                .findFirst()
                .orElseThrow(() -> new PrintError("cannot find a running task on printer with ID " + printerId));

        removeTask(foundEntry, foundEntry.getValue());
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType filamentType) throws PrintError {
        Print print = Optional.ofNullable(printerManager.findPrint(printName))
                .orElseThrow(() -> new PrintError("Could not find print with name " + printName));

        if (colors.isEmpty()) {
            throw new PrintError("no colors available");
        }

        Set<String> availableColors = printerManager.getSpools().stream()
                .filter(spool -> spool.getFilamentType() == filamentType)
                .map(Spool::getColor)
                .collect(Collectors.toSet());

        List<String> missingColors = colors.stream()
                .filter(color -> !availableColors.contains(color)).toList();

        if (!missingColors.isEmpty()) {
            throw new PrintError("Colors not found for filament type " + filamentType + ": " + missingColors);
        }
        PrintTask task = new PrintTask(print, colors, filamentType);
        printTaskManager.addPendingPrintTask(task);
        System.out.println("Added task to queue");
    }


    private void removeTask(Map.Entry<Printer, PrintTask> foundEntry, PrintTask task) {
        Printer printer = foundEntry.getKey();

        printTaskManager.getAllRunningTasks().remove(printer);
        System.out.println("Task " + task + " removed from printer " + printer.getName());

        List<Spool> spools = Arrays.asList(printer.getCurrentSpools());
        List<Double> filamentLengths = task.getPrint().getFilamentLength();

        int minSize = Math.min(spools.size(), task.getColors().size());

        IntStream.range(0, minSize)
                .forEach(i -> spools.get(i).reduceLength(filamentLengths.get(i)));

        printTaskManager.selectPrintTask(printer);
    }


    public void startPrintQueue() {
        printTaskManager.startQueue();
    }

    public List<Printer> getPrinters() {
        return new ArrayList<>(printerManager.getPrinters());
    }

    public List<Print> getPrints() {
        return new ArrayList<>(printerManager.getPrints());
    }

    public List<Spool> getSpools() {
        return new ArrayList<>(printerManager.getSpools());
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        return printTaskManager.getPrinterCurrentTask(printer);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return printTaskManager.getPendingPrintTasks();
    }

    public String getCurrentStrategy() {
        return printStrategy;
    }

    private int getPrinterType(Printer printer) {
        return switch (printer) {
            case HousedPrinter housedPrinter -> 2;
            case MultiColor multiColor -> 3;
            case StandardFDM standardFDM -> 1;
            default -> -1;
        };
    }
}
