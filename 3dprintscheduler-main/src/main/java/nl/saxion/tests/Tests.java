package nl.saxion.tests;

import nl.saxion.Facade;
import org.junit.Assert.*;
import org.junit.jupiter.api.Test;


public class Tests {
    private final Facade facade = Facade.getInstance();

    @Test
    void When_adding_print_should_be_assigned_to_printer_that_can_print_it_that_hasmatchingspool() {
        facade.addNewPrintTask();
    }

    @Test
    void When_adding_print_it_should_be_assigned_to_printer_that_can_physically_print_it_and_a_spool_change_is_initiated() {

    }

    @Test
    void When_adding_a_print_and_there_is_no_printer_available_that_can_physically_print_it_is_added_to_the_queue() {

    }

    @Test
    void When_a_printer_is_marked_as_ready_a_print_is_selected_that_fits_and_uses_the_same_spool() {

    }

    @Test
    void Starting_the_queue_will_attempt_to_fill_all_printers() {

    }

    @Test
    void When_adding_multiple_prints_in_the_order_of_Blue_Red_Blue_and_a_correct_printer_has_a_Blue_spool_It_will_select_the_third_print_when_it_is_marked_ready() {

    }

    @Test
    void print_and_printer_truthtable(){

    }


}
