package nl.saxion.Models.prints;

import nl.saxion.Models.spools.FilamentType;

import java.util.List;
import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintTask other = (PrintTask) obj;
        return Objects.equals(this.print(), other.print()) &&
                Objects.equals(this.colors(), other.colors()) &&
                this.filamentType() == other.filamentType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(print(), colors(), filamentType());
    }

    @Override
    public String toString() {
        return "< " + print.getName() + " " + filamentType + " " + colors.toString() + " >";
    }
}
