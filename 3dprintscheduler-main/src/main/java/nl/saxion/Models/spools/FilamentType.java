package nl.saxion.Models.spools;

public enum FilamentType {
    PLA, PETG, ABS;

    /**
     * Maps a filament type string to a FilamentType enum.
     *
     * @param typeString The filament type as a string.
     * @return The corresponding FilamentType.
     * @throws IllegalArgumentException If the string does not map to a valid type.
     */
    public static FilamentType fromTypeString(String typeString) {
        return switch (typeString.toUpperCase()) {
            case "PLA" -> PLA;
            case "PETG" -> PETG;
            case "ABS" -> ABS;
            default -> throw new IllegalArgumentException("Invalid filament type: " + typeString);
        };
    }
}