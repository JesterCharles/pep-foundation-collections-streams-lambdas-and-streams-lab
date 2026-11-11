package reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads orders out of the database over JDBC. Tasks 1 and 8 are in this file.
 * The connection always comes from the caller, which also closes it.
 */
public final class OrderLoader {

    /** Given. Every order, every column, in code order. */
    static final String ALL_ORDERS_SQL = "SELECT code, customer, region, status, cents FROM orders ORDER BY code";

    private OrderLoader() {
    }

    /** Task 1: loads every row of the orders table into a list of Order records, in code order. */
    public static List<Order> loadAll(Connection connection) throws SQLException {
        List<Order> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(ALL_ORDERS_SQL)) {
            // TODO Task 1: move the cursor forward one row at a time until there are no rows left.
            // For each row, read the five columns by name with the ResultSet getter that matches
            // each column's type, and add one new Order to rows.
        }
        return rows;
    }

    /**
     * Task 8: the finance report. For each customer whose OPEN orders add up to more than minCents,
     * one entry customer -> that open total, customers A to Z.
     *
     * The program needs only these few totals, not the rows, so the database should do the reducing:
     * filter rows with WHERE, total them per customer with GROUP BY and SUM, keep only the large
     * totals with HAVING, and sort with ORDER BY. The method below is given; you write the query.
     */
    // TODO Task 8: replace this placeholder query. Keep the two output columns named customer and
    // total, and keep exactly one ? parameter, the minimum, in the HAVING clause.
    static final String OPEN_TOTALS_SQL =
            "SELECT customer, cents AS total FROM orders WHERE status = 'TODO' AND cents > ?";

    /** Given. Runs OPEN_TOTALS_SQL with minCents as its parameter and collects customer -> total in row order. */
    public static Map<String, Integer> openTotalsAbove(Connection connection, int minCents) throws SQLException {
        Map<String, Integer> totals = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(OPEN_TOTALS_SQL)) {
            statement.setInt(1, minCents);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    totals.put(rs.getString("customer"), rs.getInt("total"));
                }
            }
        }
        return totals;
    }
}
