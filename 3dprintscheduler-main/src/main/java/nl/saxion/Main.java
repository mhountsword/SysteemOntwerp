package nl.saxion;

import nl.saxion.exceptions.PrintError;

import java.util.*;

public class Main {
    Scanner scanner = new Scanner(System.in);
    private final Facade facade = Facade.getInstance();
    public static void main(String[] args){
        new Main().run();
    }

    public void run(){
        int choice = 1;
        while (choice > 0 && choice <= 10) {
            menu();
            choice = scanner.nextInt();
            System.out.println("-----------------------------------");
            switch (choice) {
                case 1 -> facade.addNewPrintTask();
                case 2, 3 -> facade.registerPrintCompletion();
                case 4 ->   facade.changePrintStrategy();
                case 5 -> facade.startPrintQueue();
                case 6 -> facade.showPrints();
                case 7 -> facade.showPrinters();
                case 8 -> facade.showSpools();
                case 9 ->  facade.showPendingPrintTasks();
                case 10 -> facade.showstats();
            }
            facade.exit();
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
        System.out.println("-10) show statistics");
        System.out.println("- 0) Exit");
    }

}
