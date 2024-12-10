package nl.saxion.newModels;

import nl.saxion.Models.MultiColor;
import nl.saxion.Models.oldMultiColor;
import nl.saxion.Models.Printer;
import nl.saxion.Models.StandardFDM;

import java.awt.print.PrinterException;

public class PrinterFactory {
    public Printer addPrinter(String[] values) throws PrinterException {
        Printer result;
        if (values[1].equals("3")) {
            result = new MultiColor(values);
        } else {
            result = new StandardFDM(values);
        }
        return result;
    }
}
