package nl.saxion.models.strategy;

import nl.saxion.models.printers.Printer;

public interface PrintStrategyInterface {
    void assignTasksToPrinters(Printer printer);
}
