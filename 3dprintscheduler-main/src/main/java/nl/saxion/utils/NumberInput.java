package nl.saxion.utils;

import java.util.Scanner;

public class NumberInput {
    static Scanner scanner = new Scanner(System.in);

    public static int numberInput(int min, int max) {
        int input = scanner.nextInt();
        while (input < min || input > max) {
            input = scanner.nextInt();
        }
        return input;
    }
}
