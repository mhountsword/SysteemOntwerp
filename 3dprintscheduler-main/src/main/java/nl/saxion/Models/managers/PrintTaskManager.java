package nl.saxion.Models.managers;

import nl.saxion.Models.printers.HousedPrinter;
import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.spools.Spool;
import nl.saxion.exceptions.PrintError;
import nl.saxion.utils.NumberInput;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrintTaskManager {

    private final static PrinterManager printerManager = PrinterManager.getInstance();
    private final static PrintManager printManager = PrintManager.getInstance();
    private final static SpoolManager spoolManager = SpoolManager.getInstance();

    private final List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private final List<Printer> freePrinters = printerManager.getFreePrinters();
    private final List<Spool> freeSpools = spoolManager.getFreeSpools();
    private final Map<Printer, PrintTask> runningPrintTasks = new HashMap<>();
    private static PrintTaskManager instance;


    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        if(spools[0] != null) {
            label:
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.printFits(printTask.print())) {
                    switch (printer) {
                        case StandardFDM standardFDM when printTask.filamentType() != FilamentType.ABS && printTask.colors().size() == 1 -> {
                            if (spools[0].spoolMatch(printTask.colors().getFirst(), printTask.filamentType())) {
                                runningPrintTasks.put(printer, printTask);
                                freePrinters.remove(printer);
                                chosenTask = printTask;
                                break label;
                            }
                            // The housed printer is the only one that can print ABS, but it can also print the others.
                        }
                        case HousedPrinter housedPrinter when printTask.colors().size() == 1 -> {
                            if (spools[0].spoolMatch(printTask.colors().getFirst(), printTask.filamentType())) {
                                runningPrintTasks.put(printer, printTask);
                                freePrinters.remove(printer);
                                chosenTask = printTask;
                                break label;
                            }
                            // For multicolor the order of spools does matter, so they have to match.
                        }
                        case MultiColor multiColor when printTask.filamentType() != FilamentType.ABS && printTask.colors().size() <= multiColor.getMaxColors() -> {
                            boolean printWorks = true;
                            for (int i = 0; i < spools.length && i < printTask.colors().size(); i++) {
                                if (!spools[i].spoolMatch(printTask.colors().get(i), printTask.filamentType())) {
                                    printWorks = false;
                                }
                            }
                            if (printWorks) {
                                runningPrintTasks.put(printer, printTask);
                                freePrinters.remove(printer);
                                chosenTask = printTask;
                                break label;
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
        }
        if(chosenTask != null) {
            pendingPrintTasks.remove(chosenTask);
            System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
        } else {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            for(PrintTask printTask: pendingPrintTasks) {
                if(printer.printFits(printTask.print()) && getPrinterCurrentTask(printer) == null) {
                    switch (printer) {
                        case StandardFDM standardFDM when printTask.filamentType() != FilamentType.ABS && printTask.colors().size() == 1 -> {
                            chosenTask = getPrintTask(printer,  printTask);
                        }
                        case HousedPrinter housedPrinter when printTask.colors().size() == 1 -> {
                            chosenTask = getPrintTask(printer, printTask);
                        }
                        case MultiColor multiColor when printTask.filamentType() != FilamentType.ABS && printTask.colors().size() <= multiColor.getMaxColors() -> {
                            ArrayList<Spool> chosenSpools = new ArrayList<>();
                            for (int i = 0; i < printTask.colors().size(); i++) {
                                for (Spool spool : freeSpools) {
                                    if (spool.spoolMatch(printTask.colors().get(i), printTask.filamentType()) && !containsSpool(chosenSpools, printTask.colors().get(i))) {
                                        chosenSpools.add(spool);
                                    }
                                }
                            }
                            // We assume that if they are the same length that there is a match.
                            if (chosenSpools.size() == printTask.colors().size()) {
                                runningPrintTasks.put(printer, printTask);
                                freeSpools.addAll(Arrays.asList(printer.getCurrentSpools()));
                                printer.setCurrentSpools(chosenSpools);
                                int position = 1;
                                for (Spool spool : chosenSpools) {
                                    System.out.println("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
                                    freeSpools.remove(spool);
                                    position++;
                                }
                                freePrinters.remove(printer);
                                chosenTask = printTask;
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
            if(chosenTask != null) {
                pendingPrintTasks.remove(chosenTask);
                System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
            }
        }
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
            int printNumber = NumberInput.numberInput(1, prints.size());

            Print print = prints.get(printNumber - 1);

            System.out.println("---------- Filament Type ----------");
            System.out.println("- 1: PLA");
            System.out.println("- 2: PETG");
            System.out.println("- 3: ABS");
            System.out.print("- Filament type number: ");
            int filamentType = NumberInput.numberInput(1, 3);
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

    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }
    private PrintTask getPrintTask(Printer printer, PrintTask printTask) {
        // Find the first matching spool
        Spool chosenSpool = freeSpools.stream()
                .filter(spool -> spool.spoolMatch(printTask.colors().getFirst(), printTask.filamentType()))
                .findFirst()
                .orElse(null);

        if (chosenSpool == null) return null; // No matching spool found

        // Update printer with the new spool
        runningPrintTasks.put(printer, printTask);
        freeSpools.add(printer.getCurrentSpools()[0]); // Store the old spool back into freeSpools
        System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
        freeSpools.remove(chosenSpool);
        printer.setCurrentSpool(chosenSpool);
        freePrinters.remove(printer);

        return printTask;
    }

    public void startQueue() {
        for(Printer printer : printerManager.getPrinters()) {
            selectPrintTask(printer);
        }
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }

    public Map<Printer, PrintTask> getAllRunningTasks() {
        return runningPrintTasks;
    }

    public void addPendingPrintTask(PrintTask printTask) {
        pendingPrintTasks.add(printTask);
    }

    public void registerPrintCompletion(int printerId) throws PrintError {
        Map.Entry<Printer, PrintTask> foundEntry = getAllRunningTasks().entrySet().stream()
                .filter(entry -> entry.getKey().getId() == printerId)
                .findFirst()
                .orElseThrow(() -> new PrintError("cannot find a running task on printer with ID " + printerId));

        removeTask(foundEntry, foundEntry.getValue());
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType filamentType) throws PrintError {
        Print print = Optional.ofNullable(printManager.findPrint(printName))
                .orElseThrow(() -> new PrintError("Could not find print with name " + printName));

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

        List<Spool> spools = Arrays.asList(printer.getCurrentSpools());
        List<Double> filamentLengths = task.print().getFilamentLength();

        int minSize = Math.min(spools.size(), task.colors().size());

        IntStream.range(0, minSize)
                .forEach(i -> spools.get(i).reduceLength(filamentLengths.get(i)));

        selectPrintTask(printer);
    }

    public void printPendingPrintTasks() {
        System.out.println("--------- Pending Print Tasks ---------");
        pendingPrintTasks.forEach(printTask -> System.out.println(printTask.toString()));
        System.out.println("--------------------------------------");
    }

    public static PrintTaskManager getInstance() {
        if (instance == null) {
            instance = new PrintTaskManager();
        }
        return instance;
    }
}