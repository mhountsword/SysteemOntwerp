package nl.saxion.Models.strategy;

import nl.saxion.Models.managers.PrintTaskManager;
import nl.saxion.Models.managers.PrinterManager;
import nl.saxion.Models.managers.SpoolManager;
import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultStrategy implements PrintStrategyInterface {
    private final PrintTaskManager printTaskManager = PrintTaskManager.getInstance();
    private final PrinterManager printerManager = PrinterManager.getInstance();
    private final SpoolManager spoolManager = SpoolManager.getInstance();

    private final Map<Printer, PrintTask> runningPrintTasks = printTaskManager.getAllRunningTasks();
    private final List<PrintTask> pendingPrintTasks = printTaskManager.getPendingPrintTasks();
    private final List<Printer> freePrinters = printerManager.getFreePrinters();
    private final List<Spool> freeSpools = spoolManager.getFreeSpools();

    @Override
    public void assignTasksToPrinters(Printer printer) {
        List<Spool> currentSpools = printer.getCurrentSpools();
        PrintTask assignedTask = null;

        // Attempt to assign using the printer’s current spools.
        if (currentSpools.getFirst() != null) {
            assignedTask = tryAssignTaskWithCurrentSpools(printer, currentSpools);
        }

        // If no task was assigned, try assigning using free spools.
        if (assignedTask == null) {
            assignedTask = tryAssignTaskWithFreeSpools(printer);
        }

        if (assignedTask != null) {
            printTaskManager.removePendingPrintTask(assignedTask);
            System.out.println("- Started task: " + assignedTask + " on printer " + printer.getName());
        }
    }

    private PrintTask tryAssignTaskWithCurrentSpools(Printer printer, List<Spool> currentSpools) {
        for (PrintTask task : pendingPrintTasks) {
            if (printer.printFits(task.print())) continue;
            if (matchesCurrentSpools(printer, task, currentSpools)) {
                assignTask(printer, task);
                return task;
            }
        }
        return null;
    }

    private boolean matchesCurrentSpools(Printer printer, PrintTask task, List<Spool> currentSpools) {
        // StandardFDM: current spool must match for non-ABS prints with one color.
        if (printer instanceof StandardFDM && task.filamentType() != FilamentType.ABS && task.colors().size() == 1) {
            return currentSpools.getFirst().spoolMatch(task.colors().getFirst(), task.filamentType());
        }
        // HousedPrinter: similar to StandardFDM.
        else if (printer.isHoused() && task.colors().size() == 1) {
            return currentSpools.getFirst().spoolMatch(task.colors().getFirst(), task.filamentType());
        }
        // MultiColor: all required spools (in order) must match.
        else if (printer instanceof MultiColor multi) {
            if (task.filamentType() == FilamentType.ABS || task.colors().size() > multi.getMaxColors()) {
                return false;
            }
            for (int i = 0; i < Math.min(currentSpools.size(), task.colors().size()); i++) {
                if (!currentSpools.get(i).spoolMatch(task.colors().get(i), task.filamentType())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private PrintTask tryAssignTaskWithFreeSpools(Printer printer) {
        if (getPrinterCurrentTask(printer) != null) {
            return null;
        }

        for (PrintTask task : pendingPrintTasks) {
            if (printer.printFits(task.print())) continue;
            switch (printer) {
                case StandardFDM standardFDM when task.filamentType() != FilamentType.ABS && task.colors().size() == 1 -> {
                    PrintTask assigned = getPrintTask(printer, task);
                    if (assigned != null) return assigned;
                }
                case MultiColor multi -> {
                    if (task.filamentType() == FilamentType.ABS || task.colors().size() > multi.getMaxColors()) {
                        continue;
                    }
                    List<Spool> chosenSpools = chooseFreeSpoolsForMultiColor(task);
                    if (chosenSpools.size() == task.colors().size()) {
                        // Return the current spools to the free pool before switching.
                        freeSpools.addAll(printer.getCurrentSpools());
                        printer.setCurrentSpools(chosenSpools);
                        int position = 1;
                        for (Spool spool : chosenSpools) {
                            System.out.println("- Spool change: Please place spool " + spool.getId() +
                                    " in printer " + printer.getName() + " position " + position);
                            freeSpools.remove(spool);
                            position++;
                        }
                        assignTask(printer, task);
                        return task;
                    }
                }
                default -> {
                    System.err.println("task not found / printer invalid");
                }
            }
        }
        return null;
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
        freeSpools.add(printer.getCurrentSpools().getFirst()); // Store the old spool back into freeSpools
        System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
        freeSpools.remove(chosenSpool);
        printer.setCurrentSpool(chosenSpool);
        freePrinters.remove(printer);

        return printTask;
    }

    private List<Spool> chooseFreeSpoolsForMultiColor(PrintTask task) {
        List<Spool> chosenSpools = new ArrayList<>();
        for (String color : task.colors()) {
            for (Spool spool : freeSpools) {
                if (spool.spoolMatch(color, task.filamentType()) && !containsSpool(chosenSpools, color)) {
                    chosenSpools.add(spool);
                    break; // Use one spool per required color.
                }
            }
        }
        return chosenSpools;
    }

    private void assignTask(Printer printer, PrintTask task) {
        runningPrintTasks.put(printer, task);
        freePrinters.remove(printer);
    }

    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }


}
