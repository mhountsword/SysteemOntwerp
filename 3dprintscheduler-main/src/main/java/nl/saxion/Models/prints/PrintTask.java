package nl.saxion.Models.prints;

import nl.saxion.Models.spools.FilamentType;

import java.util.List;

public record PrintTask(Print print, List<String> colors, FilamentType filamentType) {

    @Override
    public Print print() {
        return print;
    }

    @Override
    public List<String> colors() {
        return colors;
    }

    @Override
    public FilamentType filamentType() {
        return filamentType;
    }

    @Override
    public String toString() {
        return "< " + print.getName() + " " + filamentType + " " + colors.toString() + " >";
    }
}
