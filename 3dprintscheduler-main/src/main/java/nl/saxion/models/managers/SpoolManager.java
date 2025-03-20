package nl.saxion.models.managers;

import nl.saxion.models.spools.FilamentType;
import nl.saxion.models.spools.Spool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<Spool> getFreeSpools() { // For returning used spools back to pool
        return freeSpools;
    }

    public void removeFreeSpool(Spool spool) {
        freeSpools.remove(spool);
    }

    public void returnSpool(Spool spool) {
        freeSpools.add(spool);
    }

    public void printSpools(){
        System.out.println("--------- Spools ---------");
        spools.forEach(spool -> System.out.println(spool.toString()));
        System.out.println("--------------------------------------");
    }

    // Only return unique spool colours, and sort alphabetically
    public List<Spool> getFilteredSpools(FilamentType type) {
        return spools.stream()
                .filter(spool -> spool.getFilamentType().equals(type))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Spool::getColor,    // key by color
                                Function.identity(), // value is the spool itself
                                (existing, replacement) -> existing // keep first occurrence
                        ),
                        map -> map.values().stream()
                                .sorted(Comparator.comparing(Spool::getColor))
                                .collect(Collectors.toList())
                ));
    }

    public static SpoolManager getInstance() {
        if (instance == null) {
            instance = new SpoolManager();
        }
        return instance;
    }
}
