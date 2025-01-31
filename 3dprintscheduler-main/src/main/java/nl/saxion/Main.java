package nl.saxion;

import nl.saxion.Models.prints.Print;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.spools.Spool;
import nl.saxion.exceptions.PrintError;

import java.util.*;

public class Main {
    Scanner scanner = new Scanner(System.in);
    private final Facade facade = new Facade();

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        facade.initialize();  // Initialize everything using the Facade

        int choice = 1;
        while (choice > 0 && choice < 10) {
            menu();
            choice = menuChoice(9);
            System.out.println("-----------------------------------");
            switch (choice) {
                case 1: addNewPrintTask(); break;
                case 2, 3: registerPrintCompletion(); break;
                case 4: changePrintStrategy(); break;
                case 5: startPrintQueue(); break;
                case 6: showPrints(); break;
                case 7: showPrinters(); break;
                case 8: showSpools(); break;
                case 9: showPendingPrintTasks(); break;
            }
        }
    }

    public void menu() {
        System.out.println("------------- Menu ----------------");
        System.out.println("- 1) Add new Print Task");
        System.out.println("- 2) Register Printer Completion");
        System.out.println("- 3) Register Printer Failure");
        System.out.println("- 4) Change printing style");
        System.out.println("- 5) Start Print Queue");
        System.out.println("- 6) Show prints");
        System.out.println("- 7) Show printers");
        System.out.println("- 8) Show spools");
        System.out.println("- 9) Show pending print tasks");
        System.out.println("- 0) Exit");
    }

    private void startPrintQueue() {
        System.out.println("---------- Starting Print Queue ----------");
        facade.startPrintQueue();
        System.out.println("-----------------------------------");
    }

    private void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " + facade.getCurrentStrategy());
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.print("- Choose strategy: ");
        int strategyChoice = numberInput(1, 2);
        facade.changePrintStrategy(strategyChoice);
        System.out.println("-----------------------------------");
    }

    private void registerPrintCompletion() {
        try{
            List<Printer> printers = facade.getPrinters();
            System.out.println("---------- Currently Running Printers ----------");
            for (Printer p : printers) {
                PrintTask printerCurrentTask = facade.getPrinterCurrentTask(p);
                if (printerCurrentTask != null) {
                    System.out.println("- " + p.getId() + ": " + p.getName() + " - " + printerCurrentTask);
                }
            }
            System.out.print("- Printer that is done (ID): ");
            int printerId = numberInput(-1, printers.size());
            facade.registerPrintCompletion(printerId);
            System.out.println("-----------------------------------");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void addNewPrintTask() {
        try{
            System.out.println("---------- New Print Task ----------");
            List<String> colors = new ArrayList<>();
            List<Print> prints = facade.getPrints();
            int counter = 1;
            for (Print p : prints) {
                System.out.println("- " + counter + ": " + p.getName());
                counter++;
            }
            System.out.print("- Print number: ");
            int printNumber = numberInput(1, prints.size());
            Print print = facade.getPrints().get(printNumber - 1);
            System.out.println("---------- Filament Type ----------");
            System.out.println("- 1: PLA");
            System.out.println("- 2: PETG");
            System.out.println("- 3: ABS");
            System.out.print("- Filament type number: ");
            int filamentType = numberInput(1, 3);
            FilamentType type = switch (filamentType) {
                case 2 -> FilamentType.PETG;
                case 3 -> FilamentType.ABS;
                default -> FilamentType.PLA;
            };
            facade.addPrintTask(print.getName(), colors, type);
            System.out.println("----------------------------");
        } catch (PrintError e) {
            System.out.println(e.getMessage());
        }
    }

    private void showPrints() {
        var prints = facade.getPrints();
        System.out.println("---------- Available prints ----------");
        for (Print p : prints) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    private void showSpools() {
        var spools = facade.getSpools();
        System.out.println("---------- Spools ----------");
        for (Spool spool : spools) {
            System.out.println(spool);
        }
        System.out.println("----------------------------");
    }

    private void showPrinters() {
        var printers = facade.getPrinters();
        System.out.println("--------- Available printers ---------");
        for (Printer p : printers) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    private void showPendingPrintTasks() {
        var printTasks = facade.getPendingPrintTasks();
        System.out.println("--------- Pending Print Tasks ---------");
        for (PrintTask p : printTasks) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    public int menuChoice(int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print("- Choose an option: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("- Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }

    public int numberInput(int min, int max) {
        int input = scanner.nextInt();
        while (input < min || input > max) {
            input = scanner.nextInt();
        }
        return input;
    }
}
