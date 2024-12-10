package nl.saxion.newModels;
import nl.saxion.Models.MultiColor;
import nl.saxion.Models.Printer;
import nl.saxion.Models.StandardFDM;
import java.awt.print.PrinterException;
public class PrinterFactory {
    public Printer addPrinter(String[] values) throws PrinterException {
        Printer result;
                switch (values[1]) {
            case "1","2" -> {result = new StandardFDM(values);}   // Creates a standardFDM is housed based on type (values[1])
            case "3" -> {result = new MultiColor(values);} // Creates a multi color printer

            default -> throw new PrinterException();
        }
        return result;
    }
}
