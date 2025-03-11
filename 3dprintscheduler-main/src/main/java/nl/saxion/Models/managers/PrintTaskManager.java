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

import static nl.saxion.utils.NumberInput.numberInput;

public class PrintTaskManager {
    private final static PrinterManager printerManager = PrinterManager.getInstance();
    private final static PrintManager printManager = PrintManager.getInstance();
    private final static SpoolManager spoolManager = SpoolManager.getInstance();
    private final List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private final List<Printer> freePrinters = printerManager.getFreePrinters();
    private final List<Spool> freeSpools = spoolManager.getFreeSpools();
    private final Map<Printer, PrintTask> runningPrintTasks = new HashMap<>();
    private static PrintTaskManager instance;


    // This method checks if printer can fit the task, correctly assigns spools and starts the print
    private void startPrintTaskIfFits(Printer printer, PrintTask printTask) {
        switch (printer.getClass().getSimpleName()) {
            case "StandardFDM" -> startStandardFdmTaskIfFits(printer, printTask);
            case "HousedPrinter" -> startHousedPrinterTaskIfFits(printer, printTask);
            case "MultiColor" -> startMultiColorTaskIfFits(printer, printTask);
        }
    }

    private void startPrintTask(Printer printer, PrintTask printTask) {
        runningPrintTasks.put(printer, printTask);
        freePrinters.remove(printer);
        pendingPrintTasks.remove(printTask);
        System.out.println("- Started task: " + printTask + " on printer " + printer.getName());
    }

    private void startStandardFdmTaskIfFits(Printer printer, PrintTask printTask) {
        if (canPrintStandardFDM(printTask) && spoolsMatch(printer, printTask)) {
            startPrintTask(printer, printTask);
        }
    }

    private void startHousedPrinterTaskIfFits(Printer printer, PrintTask printTask) {
        if (canPrintMonoColor(printTask) && spoolsMatch(printer, printTask)) {
            startPrintTask(printer, printTask);
        }
    }

    private void startMultiColorTaskIfFits(Printer printer, PrintTask printTask) {
        if (canPrintMultiColor(printer, printTask) && spoolsMatch(printer, printTask)) {
            startPrintTask(printer, printTask);
        }
    }

    private boolean canPrintStandardFDM(PrintTask printTask) {
        return printTask.filamentType() != FilamentType.ABS && printTask.colors().size() == 1;
    }

    private boolean canPrintMonoColor(PrintTask printTask) {
        return printTask.colors().size() == 1;
    }

    private boolean canPrintMultiColor(Printer printer, PrintTask printTask) {
        MultiColor multiColor = (MultiColor) printer;
        return printTask.filamentType() != FilamentType.ABS && printTask.colors().size() <= multiColor.getMaxColors();
    }

    private boolean spoolsMatch(Printer printer, PrintTask printTask) {
        Spool[] spools = printer.getCurrentSpools();
        for (int i = 0; i < spools.length && i < printTask.colors().size(); i++) {
            if (!spools[i].spoolMatch(printTask.colors().get(i), printTask.filamentType())) {
                return false;
            }
        }
        return true;
    }

    public void selectPrintTask(Printer printer) {
        for (PrintTask printTask : pendingPrintTasks) {
            if(printer.printFits(printTask.print())) {
                startPrintTaskIfFits(printer, printTask);
            }
        }
    }

    public void addNewPrintTask() {
        try {
            System.out.println("---------- New Print Task ----------");
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

          List<Spool> spools = spoolManager.getFilteredSpools(type);
            for (int i = 0; i < spools.size(); i++) {
                System.out.println((i + 1) + ". " + spools.get(i).getColor());
            }
            List<String> colors = new ArrayList<>();
            for (int i = 0; i < print.getFilamentLength().size(); i++) {
                System.out.print("- Color number: ");
                int colorNumber = numberInput(1, spools.size());
                if (colorNumber < 1 || colorNumber > spools.size()) {
                    throw new PrintError("invalid color number");
                }
                Spool spool = spools.get(colorNumber - 1);
                colors.add(spool.getColor());
            }
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
        System.out.println("---------- Starting Print Queue ----------");
        for(Printer printer : printerManager.getPrinters()) {
            selectPrintTask(printer);
        }
        System.out.println("-----------------------------------");
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