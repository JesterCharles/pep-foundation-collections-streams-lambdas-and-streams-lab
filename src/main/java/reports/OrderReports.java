package reports;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The shop's reports over orders that are already loaded into a list. Tasks 2-7 are in this file;
 * each has a TODO comment. Task 2 has two (above the rule fields and in largeFor), and Task 5 has
 * one in each of its two methods.
 * Every method leaves the caller's list unchanged.
 */
public final class OrderReports {

    private OrderReports() {
    }

    /** Given. True for an order whose status is OPEN. */
    public static final Predicate<Order> IS_OPEN = order -> order.status().equals("OPEN");

    /** Given. True for an order whose status is CANCELLED. */
    public static final Predicate<Order> IS_CANCELLED = order -> order.status().equals("CANCELLED");

    /** Given. An order's region, such as "north". */
    public static final Function<Order, String> REGION = Order::region;

    // TODO Task 2: replace the four placeholders below and finish largeFor. Write IS_LARGE as a
    // lambda; build NEEDS_REVIEW, ACTIVE and REGION_TAG from rules that already exist, with the
    // default methods and, negate and andThen instead of new lambdas.

    /** Task 2: true for an order worth 10000 cents (100.00) or more. */
    public static final Predicate<Order> IS_LARGE = order -> false;

    /** Task 2: true for an order that is open and large: both rules must hold. */
    public static final Predicate<Order> NEEDS_REVIEW = order -> false;

    /** Task 2: true for every order that is not cancelled. */
    public static final Predicate<Order> ACTIVE = order -> false;

    /** Task 2: the region in capitals, such as "NORTH": REGION, then String's toUpperCase. */
    public static final Function<Order, String> REGION_TAG = order -> "";

    /**
     * Task 2: the large-order rule for one customer group. VIP customers count as large from 5000
     * cents, everyone else from 10000 cents (the same rule as IS_LARGE).
     */
    public static Predicate<Order> largeFor(boolean vip) {
        // TODO Task 2: work out the threshold first, then return a lambda that compares an order's
        // cents with it. A lambda may only use a local variable that is never reassigned.
        return order -> false;
    }

    /** Task 3: the codes of the open orders, sorted A to Z. */
    public static List<String> openCodes(List<Order> orders) {
        // TODO Task 3: one stream pipeline over orders: keep the open orders, turn each into its
        // code, sort the codes, and collect them into a list.
        return List.of();
    }

    /** Task 4: a one-line summary, such as "2 open: A-102, A-104". */
    public static String openSummary(List<Order> orders) {
        // TODO Task 4: this throws IllegalStateException: the stream in open is used a second time
        // after count, its terminal operation, already consumed it. Produce the codes only once
        // (an earlier task already can) and take the count from that result.
        Stream<Order> open = orders.stream().filter(IS_OPEN);
        long count = open.count();
        List<String> codes = open.map(Order::code).sorted().toList();
        return count + " open: " + String.join(", ", codes);
    }

    /** Task 5, part 1: how many orders have each status, such as OPEN=7. */
    public static Map<String, Long> countByStatus(List<Order> orders) {
        // TODO Task 5: group the orders by status with a grouping collector, and count each group
        // with a downstream collector.
        return Map.of();
    }

    /** Task 5, part 2: the order codes in each region, each list in the input's order. */
    public static Map<String, List<String>> codesByRegion(List<Order> orders) {
        // TODO Task 5: group by region; the downstream collector must turn each order into its
        // code before collecting the codes into a list.
        return Map.of();
    }

    /** Task 6: each customer's total order value in cents. A customer can have several orders. */
    public static Map<String, Integer> centsByCustomer(List<Order> orders) {
        // TODO Task 6: toMap throws IllegalStateException (Duplicate key) as soon as a customer
        // appears twice. State what happens to a repeated key: give toMap a merge function that
        // adds the two values.
        return orders.stream().collect(Collectors.toMap(Order::customer, Order::cents));
    }

    /** Task 7: the code of the most valuable open order, or "none" when there is no open order. */
    public static String largestOpenCode(List<Order> orders) {
        // TODO Task 7: max returns an Optional, which is empty when no order is open; get() then
        // throws NoSuchElementException. Transform the Optional's order into its code without
        // unwrapping it, then supply "none" as the fallback.
        return orders.stream()
                .filter(IS_OPEN)
                .max(Comparator.comparing(Order::cents))
                .get()
                .code();
    }
}
