package exercise.hidden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import exercise.Order;
import exercise.OrderLoader;
import exercise.OrderReports;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Held-out tests: graded on submit, never shown to the learner. The edge cases of each task. */
class HeldOutTests {

    private static int databases;

    private static Connection freshDatabase() throws SQLException {
        databases++;
        return DriverManager.getConnection(
                "jdbc:h2:mem:heldout" + databases + ";INIT=RUNSCRIPT FROM 'fixtures/orders.sql'");
    }

    /** The twelve fixture rows as records. */
    private static final List<Order> FIXTURE = List.of(
            new Order("A-101", "ana", "north", "SHIPPED", 800),
            new Order("A-102", "bo", "south", "OPEN", 990),
            new Order("A-103", "cyd", "north", "OPEN", 15000),
            new Order("A-104", "ana", "north", "OPEN", 450),
            new Order("A-105", "dee", "east", "CANCELLED", 300),
            new Order("A-106", "bo", "south", "OPEN", 12500),
            new Order("A-107", "eli", "east", "SHIPPED", 2200),
            new Order("A-108", "ana", "north", "OPEN", 9000),
            new Order("A-109", "cyd", "north", "CANCELLED", 20000),
            new Order("A-110", "fay", "west", "OPEN", 3000),
            new Order("A-111", "bo", "south", "SHIPPED", 600),
            new Order("A-112", "eli", "east", "OPEN", 10000));

    private static List<String> codesWhere(java.util.function.Predicate<Order> rule) {
        List<String> codes = new ArrayList<>();
        for (Order o : FIXTURE) {
            if (rule.test(o)) {
                codes.add(o.code());
            }
        }
        return codes;
    }

    @Test
    void task2RuleEdgeCases() {
        assertFalse(OrderReports.ACTIVE.test(FIXTURE.get(4)), "Task 2: ACTIVE must be false for the cancelled order A-105");
        assertEquals("WEST", OrderReports.REGION_TAG.apply(FIXTURE.get(9)), "Task 2: REGION_TAG of a west order must be WEST");
        assertEquals(List.of("A-103", "A-106", "A-109", "A-112"), codesWhere(OrderReports.largeFor(false)),
                "Task 2: largeFor(false) must be true from 10000 cents, like IS_LARGE");
    }

    @Test
    void task4OpenSummaryOfNoOrders() {
        assertEquals("0 open: ", OrderReports.openSummary(List.of()), "Task 4: openSummary of no orders");
    }

    @Test
    void task7LargestOpenCodeOfNoOrders() {
        assertEquals("none", OrderReports.largestOpenCode(List.of()), "Task 7: largestOpenCode of no orders must be \"none\"");
    }

    @Test
    void task8OpenTotalsWithLowerMinimum() throws SQLException {
        try (Connection connection = freshDatabase()) {
            assertEquals(List.of("ana", "bo", "cyd", "eli"),
                    new ArrayList<>(OrderLoader.openTotalsAbove(connection, 5000).keySet()),
                    "Task 8: with a 5000-cent minimum, ana, bo, cyd and eli qualify, in that order");
        }
    }
}
