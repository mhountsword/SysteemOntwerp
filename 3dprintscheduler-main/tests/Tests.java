import nl.saxion.Facade;
import nl.saxion.models.managers.*;
import nl.saxion.models.printers.MultiColor;
import nl.saxion.models.printers.Printer;
import nl.saxion.models.printers.StandardFDM;
import nl.saxion.models.prints.Print;
import nl.saxion.models.prints.PrintTask;
import nl.saxion.models.spools.FilamentType;
import nl.saxion.models.spools.Spool;
import nl.saxion.models.strategy.Strategy;
import nl.saxion.exceptions.PrintError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class Tests {
    private PrintTaskManager printTaskManager;
    private PrinterManager printerManager;
    private SpoolManager spoolManager;
    private PrintManager printManager;
    private Facade facade;
    private Method addPrintTask;


    @BeforeEach
    public void setup() throws NoSuchMethodException {
        printTaskManager = PrintTaskManager.getInstance();
        printerManager = PrinterManager.getInstance();
        spoolManager = SpoolManager.getInstance();
        printManager = PrintManager.getInstance();
        facade = Facade.getInstance();

        addPrintTask = PrintTaskManager.class.getDeclaredMethod(
                "addPrintTask",
                Print.class,
                List.class,
                FilamentType.class);
        addPrintTask.setAccessible(true);
    }

    @AfterEach
    public void after() {
        printTaskManager.getPendingPrintTasks().clear();
        printTaskManager.getRunningPrintTasks().clear();

        for (Printer printer : printerManager.getPrinters()) {
            if (!printerManager.getFreePrinters().contains(printer)) {
                printerManager.addFreePrinter(printer);
            }
        }
    }

    @Test
    public void testAddPrintAssignedToPhysicallyCompatiblePrinterWithMatchingSpool() {
        // Make sure there are no other printers available
        for (Printer printer : printerManager.getPrinters()) {
            printerManager.removeFreePrinter(printer);
        }
        // Create a test Print
        Print testPrint = new Print(
                "TestPrint1",
                100, 100, 50,
                new ArrayList<>(List.of(10.0)), 120);
        printManager.getPrints().add(testPrint);

        Spool spool = new Spool(99, "Blue", FilamentType.PLA, 200.0);
        Spool spool2 = new Spool(100, "Blue", FilamentType.PLA, 200.0);
        spoolManager.addSpool(spool);
        spoolManager.addSpool(spool2);

        Printer incompatiblePrinter = new StandardFDM(
                100,
                "IncompatiblePrinter",
                "Manufacturer",
                false,
                50, 50, 50

        );

        Printer compatiblePrinter = new StandardFDM(
                100,
                "CompatiblePrinter",
                "Manufacturer",
                false,
                200, 200, 200
        );

        incompatiblePrinter.setCurrentSpool(spool2);
        printerManager.addFreePrinter(incompatiblePrinter);
        compatiblePrinter.setCurrentSpool(spool);
        printerManager.addFreePrinter(compatiblePrinter);

        // Make sure no tasks are in the queue
        assertTrue(printTaskManager.getPendingPrintTasks().isEmpty());
        assertTrue(printTaskManager.getRunningPrintTasks().isEmpty());

        // Add a PrintTask with correct color/spool
        PrintTask task = new PrintTask(testPrint, List.of("Blue"), FilamentType.PLA);
        try {
            addPrintTask.invoke(
                    printTaskManager,
                    task.print(),
                    task.colors(),
                    task.filamentType());
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("Exception occurred while invoking addPrintTaskMethod: " + e.getMessage());
        }

        // Confirm the task was added to the queue
        assertNotNull(printTaskManager.getPendingPrintTasks());
        assertTrue(printTaskManager.getPendingPrintTasks().contains(task));

        // Start the queue to assign the task to a printer
        printTaskManager.startQueue();

        // Confirm the task was assigned to a printer
        assertTrue(printTaskManager.getRunningPrintTasks().containsValue(task));

        printerManager.removeFreePrinter(compatiblePrinter);
        printerManager.removeFreePrinter(incompatiblePrinter);
    }

    @Test
    public void testAddPrintInitiatesSpoolChangeForCompatiblePrinter() {
        Spool spool1 = new Spool(101, "Red", FilamentType.PLA, 100);
        Spool spool2 = new Spool(102, "Blue", FilamentType.PLA, 100);
        spoolManager.addSpool(spool1);
        spoolManager.addSpool(spool2);

        // Set printer to have a different color spool
        Printer printer = printerManager.getPrinters().getFirst();
        if (printer instanceof StandardFDM singleSpoolPrinter) {
            singleSpoolPrinter.setCurrentSpool(spool1);
        }

        // Make sure no tasks are in the queue
        assertTrue(printTaskManager.getPendingPrintTasks().isEmpty());
        assertTrue(printTaskManager.getRunningPrintTasks().isEmpty());

        Print testPrint = new Print(
                "TestPrint2",
                50, 50, 50,
                new ArrayList<>(List.of(20.0)),
                200);
        printManager.getPrints().add(testPrint);

        PrintTask task = new PrintTask(testPrint, List.of("Blue"), FilamentType.PLA);
        try {
            addPrintTask.invoke(
                    printTaskManager,
                    task.print(),
                    task.colors(),
                    task.filamentType());
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("Exception occurred while invoking addPrintTaskMethod: " + e.getMessage());
        }

        // Confirm the task was added to the queue
        assertNotNull(printTaskManager.getPendingPrintTasks());

        // Start the queue to assign the task to a printer
        printTaskManager.startQueue();

        // Confirm the task was assigned to a printer
        assertFalse(printTaskManager.getPendingPrintTasks().contains(task));
        assertTrue(printTaskManager.getRunningPrintTasks().containsValue(task));
    }

    @Test
    public void testAddPrintToQueueWhenNoCompatiblePrinterAvailable() {
        // Make sure there are no printers available
        for (Printer printer : printerManager.getPrinters()) {
            printerManager.removeFreePrinter(printer);
        }

        // Make sure no tasks are in the queue
        assertTrue(printTaskManager.getPendingPrintTasks().isEmpty());
        assertTrue(printTaskManager.getRunningPrintTasks().isEmpty());

        // Add a print to the queue
        Print testPrint = new Print(
                "TestPrint3",
                50, 50, 50,
                new ArrayList<>(List.of(20.0)),
                200);

        printManager.getPrints().add(testPrint);

        PrintTask task = new PrintTask(testPrint, List.of("Blue"), FilamentType.PLA);
        try {
            addPrintTask.invoke(
                    printTaskManager,
                    task.print(),
                    task.colors(),
                    task.filamentType());
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("Exception occurred while invoking addPrintTaskMethod: " + e.getMessage());
        }

        // Confirm the task was added to the queue
        assertTrue(printTaskManager.getPendingPrintTasks().contains(task));

        // Start the queue to assign the task to a printer
        printTaskManager.startQueue();

        // Confirm the task was not assigned to a printer
        assertTrue(printTaskManager.getPendingPrintTasks().contains(task));
    }

    @Test
    public void testLessSpoolChanges() throws PrintError {
        // Make sure there are no other printers available for testing purposes
        for (Printer printer : printerManager.getPrinters()) {
            printerManager.removeFreePrinter(printer);
        }

        // Create test print
        Print testPrint = new Print(
                "TestPrint4",
                100, 100, 50,
                new ArrayList<>(List.of(10.0)),
                120);
        printManager.getPrints().add(testPrint);

        Spool blueSpool = new Spool(99, "Blue", FilamentType.PLA, 200.0);
        Spool redSpool = new Spool(100, "Red", FilamentType.PLA, 200.0);

        spoolManager.addSpool(blueSpool);
        spoolManager.addSpool(redSpool);

        // Prepare a printer with a matching spool
        Printer compatiblePrinter = new StandardFDM(
                100,
                "CompatiblePrinter",
                "Manufacturer",
                false,
                200, 200, 200

        );

        // Pre-assign the blue spool to the printer
        compatiblePrinter.setCurrentSpool(blueSpool);
        printerManager.addPrinter(compatiblePrinter);

        // Make sure queues are empty
        assertTrue(printTaskManager.getPendingPrintTasks().isEmpty());
        assertTrue(printTaskManager.getRunningPrintTasks().isEmpty());

        // Add multiple PrintTasks alternating between blue and red
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                PrintTask task = new PrintTask(testPrint, List.of("Blue"), FilamentType.PLA);
                try {
                    addPrintTask.invoke(
                            printTaskManager,
                            task.print(),
                            task.colors(),
                            task.filamentType());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    fail("Exception occurred while invoking addPrintTaskMethod: " + e.getMessage());
                }
            } else {
                PrintTask task = new PrintTask(testPrint, List.of("Red"), FilamentType.PLA);
                try {
                    addPrintTask.invoke(
                            printTaskManager,
                            task.print(),
                            task.colors(),
                            task.filamentType());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    fail("Exception occurred while invoking addPrintTaskMethod: " + e.getMessage());
                }
            }
        }

        // Confirm all the tasks are added to the queue
        assertEquals(10, printTaskManager.getPendingPrintTasks().size());

        // Start the queue and confirm the first task is assigned to the printer, and is blue
        printTaskManager.startQueue();
        printTaskManager.getRunningPrintTasks().forEach((key, value) ->
                assertEquals("Blue", value.colors().getFirst()));

        for (int i = 0; i < 4; i++) {
            // Mark the printer as ready
            printTaskManager.registerPrintCompletion(100);

            // Confirm the next assigned task is another blue task
            assertEquals(1, printTaskManager.getRunningPrintTasks().size());
            printTaskManager.getRunningPrintTasks().forEach((key, value) ->
                    assertEquals("Blue", value.colors().getFirst()));
        }

        // Confirm that red is assigned after all the blue tasks are completed
        printTaskManager.registerPrintCompletion(100);
        assertEquals(1, printTaskManager.getRunningPrintTasks().size());

        printTaskManager.getRunningPrintTasks().forEach((key, value) ->
                assertEquals("Red", value.colors().getFirst()));

    }

    @Test
    public void testStartQueueFillsAllPrinters() {
        int amountOfPrinters = printerManager.getPrinters().size();

        // remove all other prints
        printManager.getPrints().clear();

        for (int i = 0; i < amountOfPrinters; i++) {
            Print testPrint = new Print(
                    "TestPrint" + i,
                    100, 100, 50,
                    new ArrayList<>(List.of(10.0)),
                    120);
            printManager.getPrints().add(testPrint);

            Spool spool = new Spool(
                    99 + i,
                    "Blue",
                    FilamentType.PLA,
                    200.0);
            spoolManager.addSpool(spool);

            PrintTask task = new PrintTask(testPrint, List.of("Blue"), FilamentType.PLA);
            try {
                addPrintTask.invoke(
                        printTaskManager,
                        task.print(),
                        task.colors(),
                        task.filamentType());
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail("Exception occurred while invoking addPrintTaskMethod: " + e.getMessage());
            }
        }

        // Make sure there are enough tasks in the queue
        assertEquals(printTaskManager.getPendingPrintTasks().size(), amountOfPrinters);

        // Start the queue
        printTaskManager.startQueue();

        // Confirm all printers are filled
        assertEquals(printTaskManager.getRunningPrintTasks().size(), amountOfPrinters);
    }

    @Test
    public void testMultiColorPrinter() {
        // Clear all printers safely
        List<Printer> printersToRemove = new ArrayList<>(printerManager.getPrinters());
        for (Printer printer : printersToRemove) {
            printerManager.removeFreePrinter(printer);
        }

        Print multiColorPrint = new Print(
                "MultiColorPrint",
                200, 200, 200,
                new ArrayList<>(List.of(10.0, 20.0, 15.0, 25.0)),
                300
        );
        printManager.getPrints().add(multiColorPrint);

        Printer singleColorPrinter = new StandardFDM(
                1000,
                "SingleColorPrinter",
                "AwesomeManufacturer",
                false,
                300, 300, 300
        );

        Printer multiColorPrinter = new MultiColor(
                999,
                "MultiColorPrinter",
                "AwesomeManufacturer",
                false,
                300, 300, 300,
                4
        );

        printerManager.addPrinter(singleColorPrinter);
        printerManager.addPrinter(multiColorPrinter);

        // Add spools (different kind of colors and filamenttypes)
        List<Spool> spools = getALotOfTestSpools();
        for (Spool spool : spools) {
            spoolManager.addSpool(spool);
        }

        // Create a PrintTask requiring 4 colors
        PrintTask multiColorTask = new PrintTask(
                multiColorPrint,
                List.of("Blue", "Red", "Green", "Yellow"),
                FilamentType.PLA
        );

        try {
            addPrintTask.invoke(
                    printTaskManager,
                    multiColorTask.print(),
                    multiColorTask.colors(),
                    multiColorTask.filamentType()
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("Exception while invoking addPrintTaskMethod: " + e.getMessage());
        }

        // Start the queue
        printTaskManager.startQueue();

        // Verify the task is assigned to the multi-spool printer
        assertEquals(1, printTaskManager.getRunningPrintTasks().size());
        printTaskManager.getRunningPrintTasks().forEach((key, value) ->
                assertEquals("MultiColorPrinter", key.getName()));
    }
    @Test
    public void testDifferentPrintsAndPrinters() throws PrintError {
        for (Printer printer : printerManager.getPrinters()) {
            printerManager.removeFreePrinter(printer);
        }
        for (Spool spool : spoolManager.getSpools()) {
            spoolManager.removeFreeSpool(spool);
        }
        StrategyManager strategyManager = StrategyManager.getInstance();
        strategyManager.printStrategy = Strategy.EFFICIENT;

        Print testPrint = new Print(
                "TestPrintSingleColor",
                100, 100, 50,
                new ArrayList<>(List.of(10.0)),
                120);
        printManager.getPrints().add(testPrint);

        Print multiColorTestPrint = new Print(
                "TestPrintMultiColor",
                200, 200, 200,
                new ArrayList<>(List.of(10.0, 20.0, 15.0, 25.0)),
                300
        );
        printManager.getPrints().add(multiColorTestPrint);

        Printer standardFDM = new StandardFDM(
                100,
                "StandardFDM",
                "StandardManufacturer",
                false,
                200, 200, 200
        );
        printerManager.addPrinter(standardFDM);

        Printer multiColorPrinter = new MultiColor(
                999,
                "MultiColorPrinter",
                "AwesomeManufacturer",
                true,
                300, 300, 300,
                4
        );
        printerManager.addFreePrinter(multiColorPrinter);

        List<Spool> spools = getALotOfTestSpools();
        for (Spool spool : spools) {
            spoolManager.addSpool(spool);
        }

        // Create random different print tasks
        PrintTask task1 = new PrintTask(testPrint, List.of("Blue"), FilamentType.ABS);
        PrintTask task2 = new PrintTask(multiColorTestPrint, List.of("Blue", "Red", "Green", "Yellow"), FilamentType.PLA);
        PrintTask task3 = new PrintTask(testPrint, List.of("Red"), FilamentType.PETG);
        PrintTask task4 = new PrintTask(multiColorTestPrint, List.of("Blue", "Red", "Green", "Yellow"), FilamentType.PLA);
        PrintTask task5 = new PrintTask(testPrint, List.of("Yellow"), FilamentType.PLA);
        PrintTask task6 = new PrintTask(testPrint, List.of("Green"), FilamentType.PETG);
        PrintTask task7 = new PrintTask(multiColorTestPrint, List.of("Blue", "Red", "Green", "Yellow"), FilamentType.PETG);
        PrintTask task8 = new PrintTask(multiColorTestPrint, List.of("Blue", "Red", "Green", "Yellow"), FilamentType.PLA);

        // Use reflection to call private addPrintTask(String, List, FilamentType)
        try {
            for (int i = 1; i <= 8; i++) {
                PrintTask task = switch (i) {
                    case 1 -> task1;
                    case 2 -> task2;
                    case 3 -> task3;
                    case 4 -> task4;

                    case 5 -> task5;
                    case 6 -> task6;
                    case 7 -> task7;
                    case 8 -> task8;
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };
                addPrintTask.invoke(
                        printTaskManager,
                        task.print(),
                        task.colors(),
                        task.filamentType()
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail("Exception while invoking addPrintTaskMethod: " + e.getMessage());
        }

        // Test if all tasks in the queue
        assertEquals(8, printTaskManager.getPendingPrintTasks().size());
        // Start the queue
        printTaskManager.startQueue();

        // Verify the tasks are assigned to the printers
        assertEquals(2, printTaskManager.getRunningPrintTasks().size());

        // Mark the printers as ready and verify the next tasks are assigned
        printTaskManager.registerPrintCompletion(100);
        printTaskManager.registerPrintCompletion(999);


        assertEquals(2, printTaskManager.getRunningPrintTasks().size());
        printTaskManager.registerPrintCompletion(100);
        printTaskManager.registerPrintCompletion(999);
        printTaskManager.registerPrintCompletion(100);
        printTaskManager.registerPrintCompletion(999); // Three multicolour prints to be completed here
        printTaskManager.registerPrintCompletion(999);
        printTaskManager.registerPrintCompletion(999);


        assertEquals(0, printTaskManager.getRunningPrintTasks().size());
        assertEquals(0, printTaskManager.getPendingPrintTasks().size());
    }

    @Test
    public void testDifferentPrintsAndPrintersBasedOnTruthTable() {
        ArrayList<PrintTask> PLAPrintTasks = initializePlaAndPetgprints();
        for (PrintTask task : PLAPrintTasks) {
            printTaskManager.addPredeterminedPrintTask(task);
        }
        ArrayList<PrintTask> ABSPrintTasks = initializeAbsPrints();
        for (PrintTask task : ABSPrintTasks) {
            printTaskManager.addPredeterminedPrintTask(task);
        }
        List<Spool> testSpools = getALotOfTestSpools();
        for (Spool spool : testSpools) {
            spoolManager.addSpool(spool);
        }

        int initialPendingTasks = printTaskManager.getPendingPrintTasks().size();
        printTaskManager.startQueue();
        for (Printer printer : new ArrayList<>(printerManager.getPrintingPrinters())) {
            try {
                printTaskManager.registerPrintCompletion(printer.getId());
            } catch (PrintError e) {
                System.err.println(e.getMessage());
            }
        }

        // Assert that some tasks have moved from pending to running
        assertTrue(printTaskManager.getPendingPrintTasks().size() < initialPendingTasks, "Some print tasks should have moved from pending to running.");
        assertTrue(!printTaskManager.getRunningPrintTasks().isEmpty() || initialPendingTasks == printTaskManager.getPendingPrintTasks().size(), "Some print tasks should be running if compatible printers are available, or no tasks should run if none are compatible.");
        // Get the total number of printers
        int totalPrinters = printerManager.getPrinters().size();

        // Assert that the number of running tasks does not exceed the number of printers
        assertTrue(printTaskManager.getRunningPrintTasks().size() <= totalPrinters, "The number of running tasks should not exceed the total number of printers.");

        for (Map.Entry<Printer, PrintTask> entry : printTaskManager.getRunningPrintTasks().entrySet()) {
            if (entry.getValue().filamentType() == FilamentType.PLA) {
                assertTrue(entry.getKey() instanceof StandardFDM || entry.getKey() instanceof MultiColor,
                        "PLA print should be assigned to a StandardFDM or MultiColor printer.");
            }
            // Check if PETG prints are assigned to compatible printers
            if (entry.getValue().filamentType() == FilamentType.PETG) {
                assertTrue(entry.getKey() instanceof StandardFDM || entry.getKey() instanceof MultiColor,
                        "PETG print should be assigned to a StandardFDM or MultiColor printer.");
            }
            if (entry.getValue().filamentType() == FilamentType.ABS) {
                assertTrue(entry.getKey().isHoused(), "ABS print should be assigned to a printer that is housed.");
                assertInstanceOf(StandardFDM.class, entry.getKey(), "ABS print should be assigned to a StandardFDM printer.");
            }
            // Check if multi-color prints are assigned to MultiColor printers
            if (entry.getValue().colors().size() > 1) {
                assertInstanceOf(MultiColor.class, entry.getKey(), "Multi-color print should be assigned to a MultiColor printer.");
            }
        }
    }

    private ArrayList<PrintTask> initializeAbsPrints() {
        List<Print> prints = printManager.getPrints();
        ArrayList<PrintTask> PLAPrintTasks = new ArrayList<>();
        PrintTask task;
        for (Print print : prints) {
            if (print.getFilamentLength().size() > 1) {
                task = new PrintTask(print, List.of("Blue", "Green", "Red"), FilamentType.ABS);
            } else {
                task = new PrintTask(print, List.of("Blue"), FilamentType.ABS);
            }
            PLAPrintTasks.add(task);
        }
        return PLAPrintTasks;
    }

    private ArrayList<PrintTask> initializePlaAndPetgprints() {
        List<Print> prints = printManager.getPrints();
        ArrayList<PrintTask> PLAPrintTasks = new ArrayList<>();
        PrintTask task;
        for (Print print : prints) {
            if (print.getFilamentLength().size() > 1) {
                task = new PrintTask(print, List.of("Blue", "Green", "Red"), FilamentType.PLA);
            } else {
                task = new PrintTask(print, List.of("Blue"), FilamentType.PLA);
            }
            PLAPrintTasks.add(task);
        }
        return PLAPrintTasks;
    }

    public List<Spool> getALotOfTestSpools() {
        Spool yellowSpoolPla = new Spool(201, "Yellow", FilamentType.PLA, 2000.0);
        Spool blueSpoolPla = new Spool(202, "Blue", FilamentType.PLA, 2000.0);
        Spool redSpoolPla = new Spool(203, "Red", FilamentType.PLA, 2000.0);
        Spool greenSpoolPla = new Spool(204, "Green", FilamentType.PLA, 2000.0);
        Spool orangeSpoolPla = new Spool(205, "Orange", FilamentType.PLA, 2000.0);
        Spool yellowSpoolPla2 = new Spool(201, "Yellow", FilamentType.PLA, 2000.0);
        Spool blueSpoolPla2 = new Spool(202, "Blue", FilamentType.PLA, 2000.0);
        Spool redSpoolPla2 = new Spool(203, "Red", FilamentType.PLA, 2000.0);
        Spool greenSpoolPla2 = new Spool(204, "Green", FilamentType.PLA, 2000.0);
        Spool orangeSpoolPla2 = new Spool(205, "Orange", FilamentType.PLA, 2000.0);
        Spool yellowSpoolPetg = new Spool(206, "Yellow", FilamentType.PETG, 2000.0);
        Spool blueSpoolPetg = new Spool(207, "Blue", FilamentType.PETG, 2000.0);
        Spool redSpoolPetg = new Spool(208, "Red", FilamentType.PETG, 2000.0);
        Spool greenSpoolPetg = new Spool(209, "Green", FilamentType.PETG, 2000.0);
        Spool orangeSpoolPetg = new Spool(210, "Orange", FilamentType.PETG, 2000.0);
        Spool yellowSpoolAbs = new Spool(211, "Yellow", FilamentType.ABS, 2000.0);
        Spool blueSpoolAbs = new Spool(212, "Blue", FilamentType.ABS, 2000.0);
        Spool redSpoolAbs = new Spool(213, "Red", FilamentType.ABS, 2000.0);
        Spool greenSpoolAbs = new Spool(214, "Green", FilamentType.ABS, 2000.0);
        Spool orangeSpoolAbs = new Spool(215, "Orange", FilamentType.ABS, 2000.0);

        List<Spool> lotsOfSpools = new ArrayList<>();
        lotsOfSpools.add(yellowSpoolPla);
        lotsOfSpools.add(blueSpoolPla);
        lotsOfSpools.add(redSpoolPla);
        lotsOfSpools.add(greenSpoolPla);
        lotsOfSpools.add(orangeSpoolPla);
        lotsOfSpools.add(yellowSpoolPla2);
        lotsOfSpools.add(blueSpoolPla2);
        lotsOfSpools.add(redSpoolPla2);
        lotsOfSpools.add(greenSpoolPla2);
        lotsOfSpools.add(orangeSpoolPla2);
        lotsOfSpools.add(yellowSpoolPetg);
        lotsOfSpools.add(blueSpoolPetg);
        lotsOfSpools.add(redSpoolPetg);
        lotsOfSpools.add(greenSpoolPetg);
        lotsOfSpools.add(orangeSpoolPetg);
        lotsOfSpools.add(yellowSpoolAbs);
        lotsOfSpools.add(blueSpoolAbs);
        lotsOfSpools.add(redSpoolAbs);
        lotsOfSpools.add(greenSpoolAbs);
        lotsOfSpools.add(orangeSpoolAbs);

        return lotsOfSpools;
    }
}