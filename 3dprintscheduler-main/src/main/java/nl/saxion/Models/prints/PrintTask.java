package nl.saxion.Models.prints;

import nl.saxion.Models.spools.FilamentType;

import java.util.List;

public record PrintTask(Print print, List<String> colors, FilamentType filamentType) {
    @Override
    public String toString() {
        return "< " + print.name() + " " + filamentType + " " + colors.toString() + " >";
    }
}
