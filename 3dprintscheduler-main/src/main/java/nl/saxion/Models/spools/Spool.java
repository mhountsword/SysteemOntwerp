package nl.saxion.Models.spools;

public class Spool {
    private final int id;
    private final String color;
    private final FilamentType filamentType;
    private double length;

    public Spool(int id, String color, FilamentType filamentType, double length) {
        this.id = id;
        this.color = color;
        this.filamentType = filamentType;
        this.length = length;
    }

    public int getId() {
        return this.id;
    }

    public double getLength() {
        return length;
    }

    public boolean spoolMatch(String color, FilamentType type) {
        return color.equals(this.color) && type == this.getFilamentType();
    }
    /**
     * This method will try to reduce the length of the spool.
     *
     * @param byLength the length to reduce the spool by
     */
    public void reduceLength(double byLength) {
        this.length -= byLength;
        if (this.length < 0) {
            this.length -= byLength;
        }
    }

    public String getColor() {
        return color;
    }

    public FilamentType getFilamentType(){
        return filamentType;
    }

    @Override
    public String toString() {
        return  "--------" + System.lineSeparator() +
                "- id: " + id + System.lineSeparator() +
                "- color: " + color + System.lineSeparator() +
                "- filamentType: " + filamentType + System.lineSeparator() +
                "- length: " + length + System.lineSeparator() +
                "--------";
    }
}
