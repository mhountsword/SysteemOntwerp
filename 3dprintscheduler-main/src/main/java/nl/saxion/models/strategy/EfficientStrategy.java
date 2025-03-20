package nl.saxion.models.strategy;

import nl.saxion.models.managers.PrintTaskManager;
import nl.saxion.models.managers.PrinterManager;
import nl.saxion.models.managers.SpoolManager;
import nl.saxion.models.printers.MultiColor;
import nl.saxion.models.printers.Printer;
import nl.saxion.models.printers.StandardFDM;
import nl.saxion.models.prints.PrintTask;
import nl.saxion.models.spools.FilamentType;
import nl.saxion.models.spools.Spool;

import java.util.*;
import java.util.stream.Collectors;

public class EfficientStrategy implements PrintStrategyInterface {
    private final PrintTaskManager printTaskManager = PrintTaskManager.getInstance();
    private final PrinterManager printerManager = PrinterManager.getInstance();
    private final SpoolManager spoolManager = SpoolManager.getInstance();

    private final Map<Printer, PrintTask> runningPrintTasks = printTaskManager.getAllRunningTasks();
    private final List<PrintTask> pendingPrintTasks = printTaskManager.getPendingPrintTasks();
    private final List<Printer> freePrinters = printerManager.getFreePrinters();
    private final List<Spool> freeSpools = spoolManager.getFreeSpools();


    @Override
    public void assignTasksToPrinters(Printer printer) {
        if (pendingPrintTasks.isEmpty() || !freePrinters.contains(printer)) {
            return;
        }

        PrintTask task = pendingPrintTasks.getFirst();

        if (printer.printFits(task.print())) {
            return; // Printer cannot handle this print
        }

        if (printer instanceof StandardFDM) {
            assignTaskToStandardFDM(printer, task);
        } else if (printer instanceof MultiColor) {
            assignTaskToMultiColor(printer, task);
        }
    }

    private void assignTaskToStandardFDM(Printer printer, PrintTask task) {
        if (task.colors().size() != 1) {
            return; // Standard FDM can only handle single color prints with this strategy
        }

        FilamentType requiredFilamentType = task.filamentType();
        String requiredColor = task.colors().getFirst();
        double requiredFilament = task.print().getFilamentLength().getFirst();

        Spool bestSpool = freeSpools.stream()
                .filter(spool -> spool.getFilamentType() == requiredFilamentType &&
                        spool.getColor().equals(requiredColor) &&
                        spool.getLength() >= requiredFilament)
                .min(Comparator.comparingDouble(Spool::getLength))
                .orElse(null);

        if (bestSpool != null) {
            // Return the current spools to the free pool before switching.
            if(!printer.getCurrentSpools().contains(null)) {
                freeSpools.addAll(printer.getCurrentSpools());
            }
            printer.setCurrentSpool(bestSpool);
            freeSpools.remove(bestSpool);
            printTaskManager.removePendingPrintTask(task);
            runningPrintTasks.put(printer, task);
            freePrinters.remove(printer);
            System.out.println("- Started task: " + task + " on printer " + printer.getName() + " with spool " + bestSpool.getId());
            System.out.println("- Spool change: Please place spool " + bestSpool.getId() + " in printer " + printer.getName());
            printer.setPrinting(true);
        }
    }

    private void assignTaskToMultiColor(Printer printer, PrintTask task) {
        if (task.filamentType() == FilamentType.ABS || task.colors().size() > ((MultiColor) printer).getMaxColors() || task.colors().size() != task.print().getFilamentLength().size()) {
            return;
        }

        List<Spool> chosenSpools = new ArrayList<>();
        List<Spool> availableSpools = new ArrayList<>(freeSpools);
        boolean possible = true;

        for (int i = 0; i < task.colors().size(); i++) {
            String color = task.colors().get(i);
            FilamentType requiredFilamentType = task.filamentType();
            double requiredFilament = task.print().getFilamentLength().get(i);

            Spool bestSpoolForColor = availableSpools.stream()
                    .filter(spool -> spool.getFilamentType() == requiredFilamentType &&
                            spool.getColor().equals(color) &&
                            spool.getLength() >= requiredFilament &&
                            !containsSpoolId(chosenSpools, spool.getId()))
                    .min(Comparator.comparingDouble(Spool::getLength))
                    .orElse(null);

            if (bestSpoolForColor != null) {
                chosenSpools.add(bestSpoolForColor);
                availableSpools.remove(bestSpoolForColor);
            } else {
                possible = false;
                break;
            }
        }

        if (possible && chosenSpools.size() == task.colors().size()) {
            printer.setCurrentSpools(chosenSpools);
            freeSpools.removeAll(chosenSpools);

            printTaskManager.removePendingPrintTask(task);
            runningPrintTasks.put(printer, task);
            freePrinters.remove(printer);

            System.out.println("- Started task: " + task + " on printer " + printer.getName() + " with spools: " +
                    chosenSpools.stream().map(Spool::getId).collect(Collectors.toList()));
            printerManager.addPrintingPrinter(printer);
            int position = 1;
            for (Spool spool : chosenSpools) {
                System.out.println("- Spool change: Please place spool " + spool.getId() +
                        " in printer " + printer.getName() + " position " + position);
                position++;
            }
        }
    }

    private boolean containsSpoolId(List<Spool> spools, int id) {
        return spools.stream().anyMatch(spool -> spool.getId() == id);
    }
}