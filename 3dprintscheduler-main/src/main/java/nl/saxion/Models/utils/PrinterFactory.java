package nl.saxion.Models.utils;

import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;

import java.awt.print.PrinterException;

public class PrinterFactory {
    public Printer addPrinter(String[] values) throws PrinterException {
        Printer result;
        if (values[1].equals("3")) {
            result = new StandardFDM(values);
//            result = new MultiColor(values);
        } else {
            result = new StandardFDM(values);
        }
        return result;
    }
}
