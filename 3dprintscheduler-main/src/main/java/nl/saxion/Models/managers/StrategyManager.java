package nl.saxion.Models.managers;

import nl.saxion.utils.NumberInput;

public class StrategyManager {
    private static StrategyManager instance;
    private String printStrategy = "Less Spool Changes";

    public void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.print("- Choose strategy: ");
        int strategyChoice = NumberInput.numberInput(1, 2);
        System.out.println("-----------------------------------");
        if (strategyChoice == 1) {
            printStrategy = "Less Spool Changes";
        } else if (strategyChoice == 2) {
            printStrategy = "Efficient Spool Usage";
        }
    }

    public static StrategyManager getInstance() {
        if (instance == null) {
            instance = new StrategyManager();
        }
        return instance;
    }
}
