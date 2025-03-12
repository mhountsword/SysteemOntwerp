package nl.saxion.Models.managers;

import nl.saxion.Models.spools.FilamentType;
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
        return freeSpools;
    }

    public void printSpools(){
        System.out.println("--------- Spools ---------");
        spools.forEach(spool -> System.out.println(spool.toString()));
        System.out.println("--------------------------------------");
    }



    public List<Spool> findMatchingSpools(List<String> colors, FilamentType filamentType) {
        List<Spool> matched = new ArrayList<>();
        for (String color : colors) {
            List<Spool> matchingSpools = freeSpools.stream()
                    .filter(spool -> spool.getColor().equals(color) && spool.getFilamentType().equals(filamentType) && spool.getLength() > 0)
                    .toList();
            matched.addAll(matchingSpools);
        }
        return matched;
    }
    public List<Spool> getFilteredSpools(FilamentType type) {
        List<Spool> filteredSpools = new ArrayList<>();
        for (Spool spool : spools) {
            if (spool.getFilamentType().equals(type)) {
                filteredSpools.add(spool);
            }
        }
        return filteredSpools;
    }

    public void getcolor(){
        spools.stream()
                .map(Spool::getColor) // Extract colors
                .distinct()           // Keep only unique ones
                .forEach(System.out::println);
    }


    public static SpoolManager getInstance() {
        if (instance == null) {
            instance = new SpoolManager();
        }
        return instance;
    }
}
