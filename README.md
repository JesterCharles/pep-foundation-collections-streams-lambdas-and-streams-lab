# Lambdas and Streams lab: a loaded-order reporting pipeline

## Goal

Finish the reporting pipeline of a small online shop in eight linked tasks.
The shop's orders live in a database table. The pipeline loads the rows over JDBC into `Order` records, builds rules from lambdas, and turns the loaded list into the reports the shop asks for every morning: open orders, a summary line, counts and groups, totals per customer and the largest open order.
The last task decides where one report belongs: the finance total is reduced in SQL, because the program needs only the totals, not the rows.
You build and test it with the real JDK, Maven and an embedded H2 database on your own computer; nothing runs in the cloud.

**Time limit: 30 minutes**, split as about 3 minutes of setup and about 22 minutes of tasks (both estimates), plus about 5 minutes of buffer.
The lab runs in two sessions: part 1 is setup and Tasks 1-5 (about 19 minutes, after the grouping page), and part 2 is Tasks 6-8 (about 11 minutes, at the start of the next session).
If you pass 30 minutes, pause and finish in self-study. You can stop after any task and continue later: your edits stay in the files.

## What you edit

Edit only these two files. Each task has a `TODO` comment (Task 2 has two, above the rule fields and in `largeFor`; Task 5 has one in each of its two methods), and every test failure line starts with the task number.

| File | Tasks | What you do there |
|---|---|---|
| `starter/src/main/java/reports/OrderLoader.java` | 1, 8 | Read the result set into records (1); write the finance query (8) |
| `starter/src/main/java/reports/OrderReports.java` | 2-7 | Write the rules (2) and fix or finish one stream report per task (3-7) |

Leave everything else alone: `starter/src/main/java/reports/Order.java` (the record), `starter/src/main/java/reports/ReportApp.java` (the demo), `fixtures/orders.sql` (the shop's data), `tests/`, `starter/pom.xml` and `expected/`.

The one test command, run from the `starter` folder, is `mvn -q -o test`.
Each failing check names the task and the next thing to fix.

## The database

There is no database server to install. The tests and the demo create an H2 database in memory (`jdbc:h2:mem:`), load the twelve orders from `fixtures/orders.sql` into it, and the database disappears when the connection closes.
The fixture is found through the path `../fixtures/orders.sql`, relative to the current folder, so the tests and the demo must run with `starter` as the current folder. Running the `mvn` commands from `starter` does exactly that.
Your code only uses the standard `java.sql` API (`Connection`, `ResultSet`, `PreparedStatement`), exactly as it would against PostgreSQL.

## Prerequisites

You need JDK 25 and Apache Maven 3.9 or later, the same setup as the earlier Java labs.
Check both in a new terminal:

```
java -version
mvn -v
```

- `java -version` must report version 25.
- `mvn -v` must report Apache Maven 3.9 or later, and its `Java version:` line must also say 25.

This lab works the same on macOS, Linux and Windows (PowerShell or Command Prompt).
No Docker, database server, cloud account or other service is needed.

## Setup (once, needs the internet)

Open a terminal in the lab's `starter` folder and run:

```
mvn -q dependency:go-offline
```

It prints nothing when it succeeds; it also downloads the H2 driver. After this step, everything else in the lab runs offline.

## Tasks

Open the `starter` folder (the one that holds `pom.xml`) as your project in VS Code or another Java IDE, so the IDE's own files stay inside it, where `starter/.gitignore` keeps them out of Git. The paths in the table above start at the lab folder, so inside the IDE they begin at `src/`. The public tests (`tests`), the shop's data (`fixtures/orders.sql`) and the expected output (`expected`) stay in the lab folder next to `starter`; Maven compiles the tests from there, the database loads the data from there, and `expected` is there for you to compare against; you can read all three with your file browser or the IDE's **File > Open**.
Run every command in a terminal whose current folder is `starter`: a new terminal in the IDE (in VS Code, **Terminal > New Terminal**) already opens there. The Maven commands are the real build and test; any run or test buttons your IDE offers are optional, and they must use `starter` as their working directory, because the tests and the demo load `../fixtures/orders.sql` relative to it.
After each task, run `mvn -q -o test` from the `starter` folder and check that no line for that task number is left.
Tasks 2-7 are tested on a fixed list of the same twelve orders, so they do not depend on Task 1.
Method references such as `Order::code` fit most of the `map` and key steps below; use them where they read more clearly than a lambda.

### Task 1: rows into records (`OrderLoader.loadAll`)

1. The statement and the result set are already opened in a try-with-resources block, which closes them for you.
2. Inside it, loop while the cursor can move to another row; the cursor method returns `false` after the last row.
3. In the loop, read the five columns by name, using the `ResultSet` getter that matches each column's type (four text columns, one whole number), and add one new `Order` to `rows`.
4. Done when no `Task 1` line appears.

### Task 2: rules from lambdas (`OrderReports`: four rule fields and `largeFor`)

1. Write `IS_LARGE` as a `Predicate<Order>` lambda: true from 10000 cents.
2. Build the next three from rules that already exist, with the interfaces' default methods instead of new lambdas: `NEEDS_REVIEW` needs `IS_OPEN` and `IS_LARGE` together (`and`), `ACTIVE` is the opposite of `IS_CANCELLED` (`negate`), and `REGION_TAG` runs `toUpperCase` on `REGION`'s result (`andThen`).
3. `largeFor(vip)` returns a rule whose threshold depends on the customer: 5000 cents for VIP customers, 10000 otherwise. Work the threshold out first, then return a lambda that uses it. If the compiler says a variable used in a lambda "must be final or effectively final", the lambda captures a variable that is assigned more than once: compute the value in one assignment.
4. Done when no `Task 2` line appears.

### Task 3: a stream pipeline (`openCodes`)

1. Write one pipeline over `orders`: keep the open orders (`filter` with an existing rule), turn each order into its code (`map`), sort the codes, and collect them into a list.
2. The middle steps are intermediate and lazy; nothing runs until the terminal operation at the end.
3. Done when no `Task 3` line appears.

### Task 4: a stream is used once (`openSummary`)

1. The method calls `count()` on the stream in `open`, then uses the same stream again. `count()` was its terminal operation, so the second use throws `IllegalStateException: stream has already been operated upon or closed`.
2. Produce the open codes only once (Task 3's method already does), and take the number from that result instead of from a second stream.
3. Done when no `Task 4` line appears.

### Task 5: grouping (`countByStatus` and `codesByRegion`)

1. `countByStatus`: collect with `Collectors.groupingBy`, using the status as the classifier and a downstream collector that counts each group. The counts are `Long` values.
2. `codesByRegion`: group by region; the downstream collector is `Collectors.mapping`, which turns each order into its code before collecting the codes into a list.
3. Done when no `Task 5` line appears.

### Task 6: repeating keys (`centsByCustomer`)

1. `toMap` with only a key and a value function throws `IllegalStateException: Duplicate key ana`, because ana has three orders.
2. State the policy for a repeated key: give `toMap` a third argument, a merge function that adds the two values.
3. Done when no `Task 6` line appears.

### Task 7: Optional without get (`largestOpenCode`)

1. `max` returns an `Optional<Order>`, which is empty when no order is open, and `get()` then throws `NoSuchElementException`.
2. Remove the `get()`. Transform the `Optional<Order>` into an `Optional<String>` with the order's code (`map` on the `Optional`), then give the fallback `"none"` with the `Optional` method for a default value.
3. Done when no `Task 7` line appears.

### Task 8: reduce in SQL (`OrderLoader.OPEN_TOTALS_SQL`)

1. The finance report needs only one total per customer, not the rows, so the database should do the reducing. The method that runs the query is given; you replace the placeholder query text.
2. Keep the output columns `customer` and `total` (the sum of `cents`, named with an alias) and exactly one `?`, the minimum. The query needs, in this order:
   - a `WHERE` clause that keeps only open rows,
   - a `GROUP BY` clause that makes one group per customer,
   - a `HAVING` clause that keeps only groups whose sum is above the `?` minimum,
   - an `ORDER BY` clause that sorts customers A to Z.
3. Done when no `Task 8` line appears and the whole test run prints nothing.

## What the tests check

Tasks 1 to 7 and the result checks of Task 8 compile and run your Java on the real JDK, against the H2 database for Tasks 1 and 8.
One check is static: the first Task 8 check reads the text of `OPEN_TOTALS_SQL` and looks for `WHERE`, `GROUP BY` and `HAVING`. It checks where the work happens, which a result alone cannot show.
A passing run proves the results for the listed inputs and edge cases. It does not check which stream or lambda style you used (for example, method references or lambdas), so compare your code with each task's steps yourself.

## Test

From the `starter` folder:

```
mvn -q -o test
```

- Before you start, all eight tests fail, and Maven reports `Tests run: 8, Failures: 8`.
- If your code does not compile, Maven prints `COMPILATION ERROR` with the file and `[line,column]`. Fix that line and run the test again.
- An `SQLException` names the problem in the query, such as an unknown column or a syntax error near a word.

## Run the reports

Run the demo in `ReportApp.main` from the `starter` folder at any time:

```
mvn -q -o compile exec:java
```

It loads the fixture into an in-memory database and prints eight report lines. On the starter, most lines show `0`, `[]` or `{}`, and the two reports that throw show `not working yet` with the exception's name; when every task is done they match `expected/test-summary.txt`.

## Expected output

When every task is complete, `mvn -q -o test` prints nothing and exits with status 0.
`expected/test-summary.txt` shows the full result, including the eight lines the demo prints.

## Offline note

Only the setup step uses the network. The test and run commands use `-o` (offline), so they never download anything.
If one reports that an artifact is missing, run the setup step once more while online.

## Reset and cleanup

The lab writes only the `starter/target` folder (compiled classes and test reports), which `starter/.gitignore` keeps out of Git. The in-memory database leaves no file behind. Delete `target` from the `starter` folder:

- macOS and Linux: `rm -rf target`
- Windows PowerShell: `Remove-Item -Recurse -Force target`
- Windows Command Prompt: `rmdir /s /q target`

To start the lab over, pull a fresh copy of the lab from the platform, or undo your edits in the two files you changed (in a Git clone, `git restore` followed by the file's path).
The downloads stay in your local Maven repository (`~/.m2/repository` by default) for later Java labs.
Anything Git lists outside `starter`, such as `.DS_Store` or `Thumbs.db`, is not part of the lab and can be deleted.

## Troubleshooting

| Symptom | What to do |
|---|---|
| `mvn: command not found`, or `'mvn' is not recognized` | Maven's `bin` folder is not on your `PATH`. Add it, then open a new terminal. |
| `mvn -v` shows a Java version other than 25, or `release version 25 not supported` | Set `JAVA_HOME` to your JDK 25 folder (or unset it), then open a new terminal. |
| `Cannot access central in offline mode`, or a missing artifact | Setup has not completed. Run `mvn -q dependency:go-offline` once while online. |
| `No suitable driver found for jdbc:h2:mem:` | The H2 driver was not downloaded or `pom.xml` was changed. Run the setup step again, and restore `pom.xml` from a fresh copy if you edited it. |
| `orders.sql` not found | The command did not run with `starter` as its current folder. Open a new terminal in the IDE (it opens in `starter`), or change into the lab's `starter` folder, then run the `mvn` command again; if you use an IDE run button instead, set its working directory to `starter`. |
| `Column "..." not found` in Task 1 or 8 | Use the exact column names `code`, `customer`, `region`, `status`, `cents`, and name the total column `total`. |
| `local variables referenced from a lambda expression must be final or effectively final` | In Task 2, compute the threshold in a single assignment before the lambda, then use that variable. |
| You have passed 30 minutes | Pause here and finish in self-study; your edits stay in the files, and each failure line still names the next task. |
