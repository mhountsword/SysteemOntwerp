package nl.saxion.newModels;

import java.util.ArrayList;
import java.util.List;

public class PrinterManager {
    List<Printer> printers = new ArrayList<>();
    List<Printer> freePrinters = new ArrayList<>();
    List<Spool> spools = new ArrayList<>();
    List<Spool> freeSpools = new ArrayList<>();

    public void startInitialQueue() {

    }

    public void addPrinter(Printer printer) {
        printers.add(printer);
    }

    public void registerPrinterFailure(int num) {
    }

    public void registerPrinterCompletion(int num) {
    }

    public void printError(String error) {
    }
    private List<PrintTask> getPrintTasks() {
        return null;
    }
}
