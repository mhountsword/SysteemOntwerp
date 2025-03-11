package nl.saxion.Models.managers;

import nl.saxion.Models.spools.Spool;

import java.util.ArrayList;
import java.util.List;

public class SpoolManager {
    private static SpoolManager instance;
    private final List<Spool> spools = new ArrayList<>();
    private final List<Spool> freeSpools = new ArrayList<>();


    public void addSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public List<Spool> getSpools() {
        return new ArrayList<>(spools);
    }

    public List<Spool> getFreeSpools() {
        return new ArrayList<>(freeSpools);
    }

    public void printSpools(){
        System.out.println("--------- Spools ---------");
        spools.forEach(spool -> System.out.println(spool.toString()));
        System.out.println("--------------------------------------");
    }

    public static SpoolManager getInstance() {
        if (instance == null) {
            instance = new SpoolManager();
        }
        return instance;
    }
}
