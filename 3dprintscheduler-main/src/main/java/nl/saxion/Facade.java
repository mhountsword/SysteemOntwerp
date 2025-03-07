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

public class Facade {
//    TODO: er zit te veel business logic in de facade.
    private final PrinterManager printerManager = PrinterManager.getInstance();
    private final PrintTaskManager printTaskManager = new PrintTaskManager();
    private String printStrategy = "Less Spool Changes";

    public void initialize() {

        Reader fileReader = new Reader();
        ArrayList<Print> prints = fileReader.readPrintsFromFile("prints.json");
        ArrayList<Spool> spools = fileReader.readSpoolsFromFile("spools.json");
        ArrayList<Printer> printers = fileReader.readPrintersFromFile("printers.json");

        for (Spool spool : spools) {
            printerManager.addSpool(spool);
        }

        for (Print print : prints) {
            printerManager.addPrint(print);
        }

        for (Printer printer : printers) {
            int maxColors = 1;
            if (printer instanceof MultiColor) {
                maxColors = ((MultiColor) printer).getMaxColors();
            }
            printerManager.addPrinter(printer.getId(), getPrinterType(printer), printer.getName(),
            printer.getManufacturer(), printer.getMaxX(), printer.getMaxY(), printer.getMaxZ(), maxColors);
        }
    }

    public void changePrintStrategy(int strategyChoice) {
        if (strategyChoice == 1) {
            printStrategy = "Less Spool Changes";
        } else if (strategyChoice == 2) {
            printStrategy = "Efficient Spool Usage";
        }
    }

    public void registerPrintCompletion(int printerId) throws PrintError {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : printTaskManager.getAllRunningTasks().entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            throw new PrintError("cannot find a running task on printer with ID " + printerId);
        }
        PrintTask task = foundEntry.getValue();
        removeTask(foundEntry, task);
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
        printTaskManager.getAllRunningTasks().remove(foundEntry.getKey());
        System.out.println("Task " + task + " removed from printer " + foundEntry.getKey().getName());
        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();

        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
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
