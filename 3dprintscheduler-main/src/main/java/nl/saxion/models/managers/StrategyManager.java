package nl.saxion.models.managers;

import nl.saxion.exceptions.PrintError;
import nl.saxion.models.printers.Printer;
import nl.saxion.models.strategy.DefaultStrategy;
import nl.saxion.models.strategy.EfficientStrategy;
import nl.saxion.models.strategy.PrintStrategyInterface;
import nl.saxion.models.strategy.Strategy;
import nl.saxion.utils.NumberInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StrategyManager {
    private static StrategyManager instance;
    public Strategy printStrategy = Strategy.DEFAULT; // Initalize on default printing strategy
    private final PrintTaskManager printTaskManager = PrintTaskManager.getInstance();
    private final HashMap<Strategy, PrintStrategyInterface> printStrategies = new HashMap<>();

    public StrategyManager() {
        printStrategies.put(Strategy.DEFAULT, new DefaultStrategy());
        printStrategies.put(Strategy.EFFICIENT, new EfficientStrategy());
    }


    public void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.print("- Choose strategy: ");
        int strategyChoice = NumberInput.numberInput(1, 2);
        System.out.println("-----------------------------------");
        if (strategyChoice == 1) {
            printStrategy = Strategy.DEFAULT;
            System.out.println("Print strategy selected: Less Spool Changes");
        } else if (strategyChoice == 2) {
            printStrategy = Strategy.EFFICIENT;
            System.out.println("Print strategy selected: Efficient Spool Usage");
        }
    }

    public void startPrinting(List<Printer> printers) throws PrintError {
        List<Printer> printersCopy = new ArrayList<>(printers); // Make a new array to avoid modification during iteration
        if(!printTaskManager.getPendingPrintTasks().isEmpty()) {
            for (Printer printer : printersCopy) {
                printStrategies.get(this.printStrategy).assignTasksToPrinters(printer);
            }
        }
    }

    public static StrategyManager getInstance() {
        if (instance == null) {
            instance = new StrategyManager();
        }
        return instance;
    }
}