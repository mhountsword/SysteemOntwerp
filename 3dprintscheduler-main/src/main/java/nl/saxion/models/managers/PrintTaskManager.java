package nl.saxion.models.managers;

import nl.saxion.models.observer.Observer;
import nl.saxion.models.printers.Printer;
import nl.saxion.models.prints.Print;
import nl.saxion.models.prints.PrintTask;
import nl.saxion.models.spools.FilamentType;
import nl.saxion.models.spools.Spool;
import nl.saxion.exceptions.PrintError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nl.saxion.utils.NumberInput.numberInput;

public class PrintTaskManager{
    private final static PrinterManager printerManager = PrinterManager.getInstance();
    private final static PrintManager printManager = PrintManager.getInstance();
    private final static SpoolManager spoolManager = SpoolManager.getInstance();
    private final static StrategyManager strategyManager = StrategyManager.getInstance();
    private final static Observer observer = Observer.getInstance();

    private final List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private final Map<Printer, PrintTask> runningPrintTasks = new HashMap<>();
    private static PrintTaskManager instance;


    public Map<Printer, PrintTask> getRunningPrintTasks() {
        return runningPrintTasks;
    }

    public void startQueue() {
        try {
            strategyManager.startPrinting(printerManager.getFreePrinters());
        } catch (PrintError e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNewPrintTask() {
        try {
            System.out.println("---------- New Print Task ----------");
            List<Print> prints = printManager.getPrints();
            List<String> colors = new ArrayList<>();

            if (prints.isEmpty()) {
                System.err.println("Error: no prints found");
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

            List<Spool> spools = spoolManager.getFilteredSpools(type);
            for (int i = 0; i < spools.size(); i++) {
                System.out.println((i + 1) + ". " + spools.get(i).getColor());
            }
            for (int i = 0; i < print.getFilamentLength().size(); i++) {
                System.out.print("- Color number: ");
                int colorNumber = numberInput(1, spools.size());

                if (colorNumber < 1 || colorNumber > spools.size()) {
                    throw new PrintError("invalid color number");
                }

                Spool spool = spools.get(colorNumber - 1);
                if(colors.contains(spool.getColor())) {
                    System.out.println("Duplicate colour selected, please select another color.");
                    i--;
                } else {
                    colors.add(spool.getColor());
                    System.out.println("Color " + colorNumber + " selected");
                }
            }
            addPrintTask(print, colors, type);
            System.out.println("----------------------------");
        } catch (PrintError e) {
            System.out.println(e.getMessage());
        }
    }

    public void addPredeterminedPrintTask(PrintTask printTask) {
        try{
            addPrintTask(printTask.print(), printTask.colors(), printTask.filamentType());
        } catch (PrintError e) {
            System.out.println(e.getMessage());
        }
    }

    private void addPrintTask(Print print, List<String> colors, FilamentType filamentType) throws PrintError {
        if (colors.isEmpty()) {
            throw new PrintError("no colors available");
        }

        Set<String> availableColors = spoolManager.getSpools().stream()
                .filter(spool -> spool.getFilamentType() == filamentType)
                .map(Spool::getColor)
                .collect(Collectors.toSet());

        List<String> missingColors = colors.stream()
                .filter(color -> !availableColors.contains(color)).toList();

        if (!missingColors.isEmpty()) {
            throw new PrintError("Colors not found for filament type " + filamentType + ": " + missingColors);
        }
        PrintTask task = new PrintTask(print, colors, filamentType);
        addPendingPrintTask(task);
        System.out.println("Added task to queue");
    }

    private void removeTask(Map.Entry<Printer, PrintTask> foundEntry, PrintTask task) {
        Printer printer = foundEntry.getKey();

        getAllRunningTasks().remove(printer);
        System.out.println("Task " + task + " removed from printer " + printer.getName());

        List<Spool> spools = printer.getCurrentSpools();
        List<Double> filamentLengths = task.print().getFilamentLength();

        int minSize = Math.min(spools.size(), task.colors().size());

        IntStream.range(0, minSize)
                .forEach(i -> spools.get(i).reduceLength(filamentLengths.get(i)));

        for (Spool spool : spools) {
            spoolManager.returnSpool(spool); // Return used spools back to pool
        }
    }

    public void registerPrintCompletion(int printerId) throws PrintError {
        Map.Entry<Printer, PrintTask> foundEntry = getAllRunningTasks().entrySet().stream()
                .filter(entry -> entry.getKey().getId() == printerId)
                .findFirst()
                .orElseThrow(() -> new PrintError("cannot find a running task on printer with ID " + printerId));

        removeTask(foundEntry, foundEntry.getValue());

        printerManager.addFreePrinter(foundEntry.getKey()); // Make printer free to print again
        strategyManager.startPrinting(printerManager.getFreePrinters());
       observer.addprints();
    }

    public Map<Printer, PrintTask> getAllRunningTasks() {
        return runningPrintTasks;
    }

    public void addPendingPrintTask(PrintTask printTask) {
        pendingPrintTasks.add(printTask);
    }

    public void removePendingPrintTask(PrintTask printTask) {
        pendingPrintTasks.remove(printTask);
    }

    public void printPendingPrintTasks() {
        System.out.println("--------- Pending Print Tasks ---------");
        pendingPrintTasks.forEach(printTask -> System.out.println(printTask.toString()));
        System.out.println("--------------------------------------");
    }

    public List<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
    }

    public static PrintTaskManager getInstance() {
        if (instance == null) {
            instance = new PrintTaskManager();
        }
        return instance;
    }
}