package nl.saxion.Models.managers;

import nl.saxion.Models.prints.Print;

import java.util.ArrayList;
import java.util.List;

public class PrintManager {
    private final List<Print> prints = new ArrayList<>();
    private static PrintManager instance;


    public void addPrint(Print print) {
        Print p = new Print(print.getName(), print.getHeight(), print.getWidth(), print.getLength(), print.getFilamentLength(), print.getPrintTime());
        prints.add(p);
    }

    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }

    public List<Print> getPrints() {
        return new ArrayList<>(prints);
    }

    public void printPrints(){
        System.out.println("---------- Available prints ----------");
        prints.forEach(print -> System.out.println(print.toString()));
        System.out.println("--------------------------------------");
    }

    public static PrintManager getInstance() {
        if (instance == null) {
            instance = new PrintManager();
        }
        return instance;
    }
}
