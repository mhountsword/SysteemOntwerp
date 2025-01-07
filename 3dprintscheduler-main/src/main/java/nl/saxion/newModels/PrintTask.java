package nl.saxion.newModels;

import nl.saxion.Models.FilamentType;

import java.util.List;

public class PrintTask {
    private Print print;
    private List<String> colors;
    private FilamentType filamentType;

    public Print getPrint() {
        return print;
    }
    public List<String> getColors() {
        return colors;
    }
    public FilamentType getFilamentType() {
        return filamentType;
    }
    public PrintTask getPrinterCurrentTask(Printer printers){
        return null;
    }
    private List<PrintTask> getPendingPrintTasks(){
        return null;
    }
}
