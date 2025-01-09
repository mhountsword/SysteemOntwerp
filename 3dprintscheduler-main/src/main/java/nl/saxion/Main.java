package nl.saxion;

import nl.saxion.Models.printers.*;
import nl.saxion.Models.spools.FilamentType;
import nl.saxion.Models.prints.Print;
import nl.saxion.Models.prints.PrintTask;
import nl.saxion.Models.spools.Spool;
import nl.saxion.Models.utils.Reader;
import java.util.*;

public class Main {
    Scanner scanner = new Scanner(System.in);
    private final PrinterManager manager = new PrinterManager();

    private String printStrategy = "Less Spool Changes";

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {
        initialize();

        int choice = 1;
        while (choice > 0 && choice < 10) {
            menu();
            choice = menuChoice(9);
            System.out.println("-----------------------------------");
            if (choice == 1) {
                addNewPrintTask();
            } else if (choice == 2) {
                registerPrintCompletion();
            } else if (choice == 3) {
                registerPrinterFailure();
            } else if (choice == 4) {
                changePrintStrategy();
            } else if (choice == 5) {
                startPrintQueue();
            } else if (choice == 6) {
                showPrints();
            } else if (choice == 7) {
                showPrinters();
            } else if (choice == 8) {
                showSpools();
            } else if (choice == 9) {
                showPendingPrintTasks();
            }
        }
        exit();
    }

    private void initialize() {
        Reader fileReader = new Reader();
        ArrayList<Print> prints = fileReader.readPrintsFromFile("prints.json");
        ArrayList<Spool> spools = fileReader.readSpoolsFromFile("spools.json");
        ArrayList<Printer> printers = fileReader.readPrintersFromFile("printers.json");

        for(Spool spool: spools) {
            manager.addSpool(spool);
        }

        //TODO: these long parameters need to go & initialize in addPrint, addPrinter, etc
        for(Print print: prints) {
            manager.addPrint(print.getName(), print.getHeight(), print.getWidth(), print.getLength(), print.getFilamentLength(), print.getPrintTime());
        }

        //TODO: this can be cleaner
        for(Printer printer: printers) {
            int maxColors = 1;
            if(printer instanceof MultiColor) {
                maxColors = ((MultiColor) printer).getMaxColors();
            }
            manager.addPrinter(printer.getId(), Integer.parseInt(getPrinterType(printer).toString()), printer.getName(), printer.getManufacturer(), printer.getMaxX(), printer.getMaxY(), printer.getMaxZ(), maxColors);
        }
    }

    private Object getPrinterType(Printer printer) {
        return switch (printer) {
            case HousedPrinter housedPrinter -> 2;
            case MultiColor multiColor -> 3;
            case StandardFDM standardFDM -> 1;
            case null, default -> -1;
        };
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
        manager.startInitialQueue();
        System.out.println("-----------------------------------");
    }

    private void exit() {

    }

    // This method only changes the name but does not actually work.
    // It exists to demonstrate the output.
    // in the future strategy might be added.
    private void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.println("- Choose strategy: ");
        int strategyChoice = numberInput(1, 2);
        if(strategyChoice == 1) {
            printStrategy = "- Less Spool Changes";
        } else if( strategyChoice == 2) {
            printStrategy = "- Efficient Spool Usage";
        }
        System.out.println("-----------------------------------");
    }

    // TODO: This should be based on which printer is finished printing.
    private void registerPrintCompletion() {
        List<Printer> printers = manager.getPrinters();
        System.out.println("---------- Currently Running Printers ----------");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= manager.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }
        System.out.print("- Printer that is done (ID): ");
        int printerId = numberInput(-1, printers.size());
        System.out.println("-----------------------------------");
        manager.registerCompletion(printerId);
    }

    private void registerPrinterFailure() {
        List<Printer> printers = manager.getPrinters();
        System.out.println("---------- Currently Running Printers ----------");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= manager.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " > " + printerCurrentTask);
            }
        }
        System.out.print("- Printer ID that failed: ");
        int printerId = numberInput(1, printers.size());

        manager.registerPrinterFailure(printerId);
        System.out.println("-----------------------------------");
    }

    private void addNewPrintTask() {
        List<String> colors = new ArrayList<>();
        var prints = manager.getPrints();
        System.out.println("---------- New Print Task ----------");
        System.out.println("---------- Available prints ----------");
        int counter = 1;
        for (Print p : prints) {
            System.out.println("- " + counter + ": " + p.getName());
            counter++;
        }

        System.out.print("- Print number: ");
        int printNumber = numberInput(1, prints.size());
        System.out.println("--------------------------------------");
        Print print = manager.findPrint(printNumber - 1);
        String printName = print.getName();
        System.out.println("---------- Filament Type ----------");
        System.out.println("- 1: PLA");
        System.out.println("- 2: PETG");
        System.out.println("- 3: ABS");
        System.out.print("- Filament type number: ");
        int ftype = numberInput(1, 3);
        System.out.println("--------------------------------------");
        FilamentType type;
        switch (ftype) {
            case 1:
                type = FilamentType.PLA;
                break;
            case 2:
                type = FilamentType.PETG;
                break;
            case 3:
                type = FilamentType.ABS;
                break;
            default:
                System.out.println("- Not a valid filamentType, bailing out");
                return;
        }
        var spools = manager.getSpools();
        System.out.println("---------- Colors ----------");
        ArrayList<String> availableColors = new ArrayList<>();
        counter = 1;
        for (Spool spool : spools) {
            String colorString = spool.getColor();
            if(type == spool.getFilamentType() && !availableColors.contains(colorString)) {
                System.out.println("- " + counter + ": " + colorString + " (" + spool.getFilamentType() + ")");
                availableColors.add(colorString);
                counter++;
            }
        }
        System.out.print("- Color number: ");
        int colorChoice = numberInput(1, availableColors.size());
        colors.add(availableColors.get(colorChoice-1));
        for(int i = 1; i < print.getFilamentLength().size(); i++) {
            System.out.print("- Color number: ");
            colorChoice = numberInput(1, availableColors.size());
            colors.add(availableColors.get(colorChoice-1));
        }
        System.out.println("--------------------------------------");

        manager.addPrintTask(printName, colors, type);
        System.out.println("----------------------------");
    }

    private void showPrints() {
        var prints = manager.getPrints();
        System.out.println("---------- Available prints ----------");
        for (Print p : prints) {
            System.out.println(p);
        }
        System.out.println("--------------------------------------");
    }

    private void showSpools() {
        var spools = manager.getSpools();
        System.out.println("---------- Spools ----------");
        for (Spool spool : spools) {
            System.out.println(spool);
        }
        System.out.println("----------------------------");
    }

    private void showPrinters() {
        var printers = manager.getPrinters();
        System.out.println("--------- Available printers ---------");
        for (Printer p : printers) {
            String output = p.toString();
            PrintTask currentTask = manager.getPrinterCurrentTask(p);
            if(currentTask != null) {
                output = output.replace("--------", "- Current Print Task: " + currentTask + System.lineSeparator() +
                        "--------");
            }
            System.out.println(output);
        }
        System.out.println("--------------------------------------");
    }

    private void showPendingPrintTasks() {
        List<PrintTask> printTasks = manager.getPendingPrintTasks();
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
                //try again after consuming the current line
                System.out.println("- Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }

    public String stringInput() {
        String input = null;
        while(input == null || input.length() == 0){
            input = scanner.nextLine();
        }
        return input;
    }

    public int numberInput() {
        int input = scanner.nextInt();
        return input;
    }

    public int numberInput(int min, int max) {
        int input = numberInput();
        while (input < min || input > max) {
            input = numberInput();
        }
        return input;
    }
}
