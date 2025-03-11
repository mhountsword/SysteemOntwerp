package nl.saxion.Models.prints;

import java.util.ArrayList;

public class Print {
    private final String name;
    private final int height;
    private final int width;
    private final int length;
    private final ArrayList<Double> filamentLength;
    private final int printTime;

    public Print(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength = filamentLength;
        this.printTime = printTime;
    }

    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Height: " + height + System.lineSeparator() +
                "- Width: " + width + System.lineSeparator() +
                "- Length: " + length + System.lineSeparator() +
                "- FilamentLength: " + filamentLength + System.lineSeparator() +
                "- Print Time: " + printTime + System.lineSeparator() +
                "--------";
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getPrintTime() {
        return printTime;
    }

    public ArrayList<Double> getFilamentLength() {
        return filamentLength;
    }
}
