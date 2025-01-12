package nl.saxion;

import nl.saxion.Models.printers.HousedPrinter;
import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.Spool;

import java.util.*;

public class PrinterManager {
    private final List<Printer> printers = new ArrayList<>();
    private final List<Print> prints = new ArrayList<>();
    private final List<Spool> spools = new ArrayList<>();
    private final List<Spool> freeSpools = new ArrayList<>();
    private final List<Printer> freePrinters = new ArrayList<>();
    private final List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private final Map<Printer, PrintTask> runningPrintTasks = new HashMap();

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) { //receive String[]
        //TODO
        //all of this should be able to go
        //replace with printerfactory
        if (printerType == 1) {
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ,true); //forward to printerfactory
            printers.add(printer);
            freePrinters.add(printer);
        } else if (printerType == 2) {
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ,false); //forward to printerfactory
//          HousedPrinter printer = new HousedPrinter(id, printerName, manufacturer, maxX, maxY, maxZ);
            printers.add(printer);
            freePrinters.add(printer);
        } else if (printerType == 3) {
            MultiColor printer = new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors, false);
            printers.add(printer);
            freePrinters.add(printer);
        }
    }

    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        if(spools[0] != null) {
            label:
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.printFits(printTask.getPrint())) {
                    switch (printer) {
                        case StandardFDM standardFDM when printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1 -> {
                            if (spools[0].spoolMatch(printTask.getColors().getFirst(), printTask.getFilamentType())) {
                                runningPrintTasks.put(printer, printTask);
                                freePrinters.remove(printer);
                                chosenTask = printTask;
                                break label;
                            }
                            // The housed printer is the only one that can print ABS, but it can also print the others.
                        }
                        case HousedPrinter housedPrinter when printTask.getColors().size() == 1 -> {
                            if (spools[0].spoolMatch(printTask.getColors().getFirst(), printTask.getFilamentType())) {
                                runningPrintTasks.put(printer, printTask);
                                freePrinters.remove(printer);
                                chosenTask = printTask;
                                break label;
                            }
                            // For multicolor the order of spools does matter, so they have to match.
                        }
                        case MultiColor multiColor when printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= multiColor.getMaxColors() -> {
                            boolean printWorks = true;
                            for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                                if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
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
                if(printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
                    switch (printer) {
                        case StandardFDM standardFDM when printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1 -> {
                            chosenTask = getPrintTask(printer, chosenTask, printTask);
                        }
                        case HousedPrinter housedPrinter when printTask.getColors().size() == 1 -> {
                            chosenTask = getPrintTask(printer, chosenTask, printTask);
                        }
                        case MultiColor multiColor when printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= multiColor.getMaxColors() -> {
                            ArrayList<Spool> chosenSpools = new ArrayList<>();
                            for (int i = 0; i < printTask.getColors().size(); i++) {
                                for (Spool spool : freeSpools) {
                                    if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                                        chosenSpools.add(spool);
                                    }
                                }
                            }
                            // We assume that if they are the same length that there is a match.
                            if (chosenSpools.size() == printTask.getColors().size()) {
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

    private PrintTask getPrintTask(Printer printer, PrintTask chosenTask, PrintTask printTask) {
        Spool chosenSpool = null;
        for (Spool spool : freeSpools) {
            if (spool.spoolMatch(printTask.getColors().getFirst(), printTask.getFilamentType())) {
                chosenSpool = spool;
            }
        }
        if (chosenSpool != null) {
            runningPrintTasks.put(printer, printTask);
            freeSpools.add(printer.getCurrentSpools()[0]);
            System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
            freeSpools.remove(chosenSpool);
            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
            freePrinters.remove(printer);
            chosenTask = printTask;
        }
        return chosenTask;
    }

    public void startInitialQueue() {
        for(Printer printer: printers) {
            selectPrintTask(printer);
        }
    }

    public void addPrint(Print print) {
        Print p = new Print(print.getName(), print.getHeight(), print.getWidth(), print.getLength(), print.getFilamentLength(), print.getPrintTime());
        prints.add(p);
    }

    public List<Print> getPrints() {
        return prints;
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
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

    public List<PrintTask> getPendingPrintTasks() {return pendingPrintTasks; }
    public void addPendingPrintTask(PrintTask printTask) {
        pendingPrintTasks.add(printTask);
    }

    public void addSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public List<Spool> getSpools() {
        return spools;
    }
}
