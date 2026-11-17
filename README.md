# Lambdas and Streams lab: a shop's morning reports

You finish the morning reports of a small online shop. The shop's orders are in a database table. Your code loads them into `Order` records, builds rules from lambdas, and turns the list into reports with streams: open orders, a summary line, counts and groups, totals per customer and the largest open order. In the last task you move the finance total into SQL, because the program needs only the totals, not the rows.

**Time:** about 30 minutes, in two sessions: Tasks 1-5 first, then Tasks 6-8. If you pass 30 minutes, stop and finish later; your edits stay in the files.

## Quick start

1. Open the lab project. The files you edit are in `src/main/java/exercise/`; the checklist below names them.
2. Select **Run Tests** before you change anything.

   **All 8 visible tests fail at first. That is expected: the TODOs are not done yet.**
   Every failure message starts with its task number, such as `Task 1:`.
3. Work through the checklist below. After each task, select **Run Tests** again and read the remaining `Task N:` messages.
4. **You are finished when all 8 visible tests pass.** Then select **Submit**.
   Submitting also runs held-out tests you can't see. They check the edge cases each task's steps describe, such as an empty list of orders.

```
OPEN project -> RUN TESTS: 8 visible tests fail (expected)
  -> READ the "Task N:" messages -> EDIT the next TODO -> RUN TESTS again
  -> repeat for Tasks 1-8 -> SUBMIT when all 8 visible tests pass
```

In words: open the project, run the tests, read which tasks fail, complete one task's TODO, run the tests again, and submit when they all pass.

## Checklist

Edit only these two files in `src/main/java/exercise/`. Each task has a `TODO Task N` comment there.

- [ ] Task 1: `OrderLoader.java`, `loadAll` (read every database row into an `Order` record)
- [ ] Task 2: `OrderReports.java`, the four rule fields and `largeFor` (one `TODO` above the fields, one in `largeFor`): build the rules from lambdas
- [ ] Task 3: `OrderReports.java`, `openCodes` (one stream pipeline)
- [ ] Task 4: `OrderReports.java`, `openSummary` (fix the stream that is used twice)
- [ ] Task 5: `OrderReports.java`, `countByStatus` and `codesByRegion` (one `TODO` in each): group the orders
- [ ] Task 6: `OrderReports.java`, `centsByCustomer` (a total per customer when a customer has several orders)
- [ ] Task 7: `OrderReports.java`, `largestOpenCode` (handle "no open order" without `get()`)
- [ ] Task 8: `OrderLoader.java`, `OPEN_TOTALS_SQL` (let the database compute the finance totals)

Tasks 2-7 use a fixed list of orders, so they do not depend on Task 1.

A task is done when no failure message starts with its number.
Leave the other files as they are:
- `Order.java`, `ReportApp.java` and `fixtures/orders.sql` are given.
- The tests in `src/test/java/exercise/` are restored to their original state before your work is graded, so changes to them do not count. Leave the build file `pom.xml` as it is.

## Hints

Read a hint only when the failure message is not enough. Method references such as `Order::code` fit most `map` and key steps; use them where they read more clearly than a lambda.

### Task 1: load the rows

<details><summary>Hint</summary>

The statement and result set are already opened for you. Loop while the cursor can move to another row (that method returns `false` after the last row). In the loop, read the five columns by name with the `ResultSet` getter that matches each column's type (four text columns, one whole number), and add one new `Order` to `rows`.

</details>

### Task 2: rules from lambdas

<details><summary>Hint</summary>

`IS_LARGE` is a `Predicate<Order>` lambda: true from 10000 cents. Build the next three from rules that already exist, with the interfaces' default methods instead of new lambdas: `NEEDS_REVIEW` needs `IS_OPEN` and `IS_LARGE` together (`and`), `ACTIVE` is the opposite of `IS_CANCELLED` (`negate`), and `REGION_TAG` runs `toUpperCase` on `REGION`'s result (`andThen`). `largeFor(vip)` uses a threshold of 5000 cents for VIP customers and 10000 otherwise: work the threshold out first, then return a lambda that uses it. If the compiler says a variable "must be final or effectively final", compute it in one assignment.

</details>

### Task 3: one pipeline

<details><summary>Hint</summary>

One pipeline over `orders`: keep the open orders (`filter` with an existing rule), turn each order into its code (`map`), sort, and collect into a list. Nothing runs until the terminal operation at the end.

</details>

### Task 4: a stream is used once

<details><summary>Hint</summary>

`count()` is the stream's terminal operation, so using the same stream again throws `IllegalStateException: stream has already been operated upon or closed`. Produce the open codes once (Task 3's method already does) and take the number from that result.

</details>

### Task 5: group the orders

<details><summary>Hint</summary>

`countByStatus`: `Collectors.groupingBy` with the status as the classifier and a downstream collector that counts each group (the counts are `Long`). `codesByRegion`: group by region with `Collectors.mapping` downstream, turning each order into its code before collecting the codes into a list.

</details>

### Task 6: repeated keys

<details><summary>Hint</summary>

`toMap` with only a key and a value function throws `IllegalStateException: Duplicate key ana`, because ana has three orders. Give `toMap` a third argument: a merge function that adds the two values.

</details>

### Task 7: Optional without get()

<details><summary>Hint</summary>

`max` returns an `Optional<Order>`, which is empty when no order is open, and `get()` then throws `NoSuchElementException`. Remove `get()`, turn the `Optional<Order>` into an `Optional<String>` with the code (`map` on the `Optional`), then give the fallback `"none"` with the `Optional` method for a default value.

</details>

### Task 8: reduce in SQL

<details><summary>Hint</summary>

The method that runs the query is given; you replace the placeholder query text. Keep the output columns `customer` and `total` (the sum of `cents`, named with an alias) and exactly one `?`, the minimum. In order, the query needs a `WHERE` clause that keeps only open rows, a `GROUP BY` clause that makes one group per customer, a `HAVING` clause that keeps only groups whose sum is above the `?` minimum, and an `ORDER BY` clause that sorts customers A to Z. Use the exact column names `code`, `customer`, `region`, `status` and `cents`.

</details>

### About the test results

Your Java runs on the real JDK, against an H2 database for Tasks 1 and 8. One Task 8 check reads the text of `OPEN_TOTALS_SQL` and looks for `WHERE`, `GROUP BY` and `HAVING`, because a result alone cannot show where the work happened. The tests do not check whether you used method references or lambdas, so compare your code with each task's hint yourself.

## The database

The tests and the demo create an H2 database in memory and load the twelve orders from `fixtures/orders.sql`; it disappears when the connection closes. No database server is needed. Your code uses only the standard `java.sql` API (`Connection`, `ResultSet`, `PreparedStatement`).

## Using an AI assistant

If you use an AI assistant, ask it to explain, not to answer.
"Explain what groupingBy's downstream collector does" teaches you collectors; "write the reports for me" does not.
Check any explanation with a test run before you trust it.

## Run the program (optional)

The demo prints eight report lines. Open `src/main/java/exercise/ReportApp.java` and select **Run** above `main`; the editor runs it from the project folder with H2 on the classpath.

On the starter, most lines show `0`, `[]` or `{}`, and two show `not working yet`. When all tasks are done it prints:

```
Loaded orders: 12
Needs review: [A-103, A-106, A-112]
Open codes: [A-102, A-103, A-104, A-106, A-108, A-110, A-112]
Open summary: 7 open: A-102, A-103, A-104, A-106, A-108, A-110, A-112
Count by status: {CANCELLED=2, OPEN=7, SHIPPED=3}
Cents by customer: {eli=12200, dee=300, ana=10250, bo=14090, fay=3000, cyd=35000}
Largest open order: A-103
Open totals above 10000 cents (from SQL): {bo=13490, cyd=15000}
```

The two maps built by stream collectors are HashMaps, so their printed order is not guaranteed; the same entries in another order are also correct.

## Start over

Undo your edits in `OrderLoader.java` and `OrderReports.java`. Compiled files go to `target/`, which you can delete at any time.

## Troubleshooting

- **`orders.sql` not found.** The tests and the demo read `fixtures/orders.sql` relative to the project folder, so run them from there. Use **Run Tests**, or run the demo with the editor's **Run**, which starts in the project folder.
- **`No suitable driver found for jdbc:h2:mem:`.** The H2 driver is missing from the Maven cache. Use **Run Tests**, and do not edit `pom.xml`.
- **`COMPILATION ERROR`.** Maven names the file and `[line,column]`. Fix that line and run the tests again.
- **An `SQLException` in Task 1 or 8.** The message names the problem, such as an unknown column or a syntax error near a word. Check the column names in the Task 8 hint.
