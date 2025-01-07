package nl.saxion.newModels;
import java.util.ArrayList;
import java.util.List;

public class PrintManager {
    List<Print> prints = new ArrayList<>();

    public List<Print> getPrints() {
        return prints;
    }

    public Print findPrint(String name) {
        for (Print print : prints) {
            if (print.getName().equalsIgnoreCase(name)) {
                return print;
            }
        }
        return null;
    }
//TODO: nog geen id in printveld. moet er op een andere int gezocht worden?
//    public Print findPrint(int id){
//        for (Print print : prints) {
//            if (print.getid == id) {
//                return print;
//            }
//        }
//        return null;
//
//    }
}


