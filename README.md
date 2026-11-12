# Lambdas and Streams lab: a shop's morning reports

You will finish the morning reports of a small online shop. The shop's orders are in a database table. Your code loads them into `Order` records, builds rules from lambdas, and turns the list into reports with streams: open orders, a summary line, counts and groups, totals per customer and the largest open order. In the last task you move the finance total into SQL, because the program needs only the totals, not the rows.

**Time:** about 30 minutes, in two sessions: Tasks 1-5 first, then Tasks 6-8. If you pass 30 minutes, stop and finish later; your edits stay in the files.

```
Open project -> Run starter tests -> Edit TODOs (Tasks 1-8) -> Rerun tests -> Read feedback -> Stop when tests pass
```

In words: open the project, run the tests once, then for each task edit its `TODO`, rerun the tests and read the feedback. Stop when every test passes.

## Quick start

1. **Open the terminal.** In the provided VS Code project, choose **Terminal > New Terminal**.
2. **Go to the `starter` folder.** Every command in this lab runs there, because the tests load the shop's data from `../fixtures/orders.sql`. (If the terminal prompt already ends in `starter`, skip this step.)

   ```
   cd starter
   ```

3. **Download the build's dependencies (once).** It prints nothing when it works:

   ```
   mvn -q dependency:go-offline
   ```

4. **Run the starter tests:**

   ```
   mvn -q -o test
   ```

   **All 8 tests fail at first. That is expected:** no task's `TODO` is done yet. Maven reports `Tests run: 8, Failures: 8`, and each failure line starts with its task number.

## Your tasks

Edit only these two files in `starter/src/main/java/reports/`. Each task has a `TODO Task N` comment there.

- [ ] **Task 1** `OrderLoader.java`, `loadAll`: read every database row into an `Order` record.
- [ ] **Task 2** `OrderReports.java`, the four rule fields and `largeFor` (one `TODO` above the fields, one in `largeFor`): build the rules from lambdas.
- [ ] **Task 3** `OrderReports.java`, `openCodes`: write one stream pipeline.
- [ ] **Task 4** `OrderReports.java`, `openSummary`: fix the stream that is used twice.
- [ ] **Task 5** `OrderReports.java`, `countByStatus` and `codesByRegion` (one `TODO` in each): group the orders.
- [ ] **Task 6** `OrderReports.java`, `centsByCustomer`: total per customer when a customer has several orders.
- [ ] **Task 7** `OrderReports.java`, `largestOpenCode`: handle "no open order" without `get()`.
- [ ] **Task 8** `OrderLoader.java`, `OPEN_TOTALS_SQL`: let the database compute the finance totals.

Tasks 2-7 use a fixed list of orders, so they do not depend on Task 1.

**After each task**, run only that task's test. Replace `1` with the task number:

```
mvn -q -o test -Dtest="OrderReportsTest#task1*"
```

It prints nothing when that task passes. If it fails, read the failure line: it names the task and what is still wrong.

**You are done when the full test command prints nothing:**

```
mvn -q -o test
```

Silence means all 8 tests pass. Leave the other files as they are: `Order.java`, `ReportApp.java`, `fixtures/orders.sql`, `tests/`, `starter/pom.xml` and `expected/`.

## Hints

Read a hint only when the failure message is not enough. Method references such as `Order::code` fit most `map` and key steps; use them where they read more clearly than a lambda.

**Task 1.** The statement and result set are already opened for you. Loop while the cursor can move to another row (that method returns `false` after the last row). In the loop, read the five columns by name with the `ResultSet` getter that matches each column's type (four text columns, one whole number), and add one new `Order` to `rows`.

**Task 2.** `IS_LARGE` is a `Predicate<Order>` lambda: true from 10000 cents. Build the next three from rules that already exist, with the interfaces' default methods instead of new lambdas: `NEEDS_REVIEW` needs `IS_OPEN` and `IS_LARGE` together (`and`), `ACTIVE` is the opposite of `IS_CANCELLED` (`negate`), and `REGION_TAG` runs `toUpperCase` on `REGION`'s result (`andThen`). `largeFor(vip)` uses a threshold of 5000 cents for VIP customers and 10000 otherwise: work the threshold out first, then return a lambda that uses it. If the compiler says a variable "must be final or effectively final", compute it in one assignment.

**Task 3.** One pipeline over `orders`: keep the open orders (`filter` with an existing rule), turn each order into its code (`map`), sort, and collect into a list. Nothing runs until the terminal operation at the end.

**Task 4.** `count()` is the stream's terminal operation, so using the same stream again throws `IllegalStateException: stream has already been operated upon or closed`. Produce the open codes once (Task 3's method already does) and take the number from that result.

**Task 5.** `countByStatus`: `Collectors.groupingBy` with the status as the classifier and a downstream collector that counts each group (the counts are `Long`). `codesByRegion`: group by region with `Collectors.mapping` downstream, turning each order into its code before collecting the codes into a list.

**Task 6.** `toMap` with only a key and a value function throws `IllegalStateException: Duplicate key ana`, because ana has three orders. Give `toMap` a third argument: a merge function that adds the two values.

**Task 7.** `max` returns an `Optional<Order>`, which is empty when no order is open, and `get()` then throws `NoSuchElementException`. Remove `get()`, turn the `Optional<Order>` into an `Optional<String>` with the code (`map` on the `Optional`), then give the fallback `"none"` with the `Optional` method for a default value.

**Task 8.** The method that runs the query is given; you replace the placeholder query text. Keep the output columns `customer` and `total` (the sum of `cents`, named with an alias) and exactly one `?`, the minimum. In order, the query needs a `WHERE` clause that keeps only open rows, a `GROUP BY` clause that makes one group per customer, a `HAVING` clause that keeps only groups whose sum is above the `?` minimum, and an `ORDER BY` clause that sorts customers A to Z. Use the exact column names `code`, `customer`, `region`, `status` and `cents`.

## Reference

**Requirements.** JDK 25 and Apache Maven 3.9 or later. To check, run `java -version` (must say 25) and `mvn -v` (Maven 3.9 or later, and its `Java version:` line must say 25). No database server, container or cloud service is needed.

**The database.** The tests and the demo create an H2 database in memory and load the twelve orders from `fixtures/orders.sql`; it disappears when the connection closes. The dependency step downloads the H2 driver. Your code uses only the standard `java.sql` API (`Connection`, `ResultSet`, `PreparedStatement`).

**Why `-o`?** The dependency step downloads the build's plugins, JUnit and H2 into your local Maven repository. After that, `-o` (offline) runs the tests and the demo without the network.

**See the reports run (optional).** From `starter`:

```
mvn -q -o compile exec:java
```

It prints eight report lines. On the starter, most show `0`, `[]` or `{}`, and two show `not working yet`. When all tasks are done they match `expected/test-summary.txt`.

**What the tests check.** Your Java runs on the real JDK, against the H2 database for Tasks 1 and 8. One Task 8 check reads the text of `OPEN_TOTALS_SQL` and looks for `WHERE`, `GROUP BY` and `HAVING`, because a result alone cannot show where the work happened. The tests do not check whether you used method references or lambdas.

**Start over.** Undo your edits in `OrderLoader.java` and `OrderReports.java`. Compiled files go to `starter/target`, which you can delete at any time.

## Troubleshooting

| Problem | What to do |
|---|---|
| `there is no POM in this directory`, or `orders.sql` not found | The terminal is not in `starter`. Run `cd starter`, then the command again. |
| `mvn: command not found` or `'mvn' is not recognized` | Maven is not available in this terminal. Ask your instructor, or see **Requirements**. |
| `mvn -v` shows a Java version other than 25, or `release version 25 not supported` | The build needs JDK 25. Ask your instructor, or point `JAVA_HOME` at a JDK 25 and open a new terminal. |
| `Cannot access central in offline mode`, a missing artifact, or `No suitable driver found for jdbc:h2:mem:` | The dependency step has not finished. Run `mvn -q dependency:go-offline` once with internet access. Do not edit `starter/pom.xml`. |
| `COMPILATION ERROR` | Maven names the file and `[line,column]`. Fix that line and run the tests again. |
| An `SQLException` in Task 1 or 8 | The message names the problem, such as an unknown column or a syntax error near a word. Check the column names in the Task 8 hint. |
