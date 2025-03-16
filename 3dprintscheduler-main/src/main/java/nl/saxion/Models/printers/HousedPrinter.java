//package nl.saxion.Models.printers;
//
//import nl.saxion.Models.spools.Spool;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HousedPrinter extends Printer{
//    public HousedPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
//        super(id, printerName, manufacturer, maxX, maxY, maxZ);
//    }
//
//    @Override
//    public List<Spool> getCurrentSpools() {
//        List<Spool> spools = new ArrayList<>();
//        spools.add(getCurrentSpool());
//        return spools;
//    }
//}
