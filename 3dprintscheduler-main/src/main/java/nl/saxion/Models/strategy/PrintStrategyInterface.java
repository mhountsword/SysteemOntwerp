package nl.saxion.Models.strategy;

import nl.saxion.Models.printers.Printer;

public interface PrintStrategyInterface {
    void assignTasksToPrinters(Printer printer);
}
