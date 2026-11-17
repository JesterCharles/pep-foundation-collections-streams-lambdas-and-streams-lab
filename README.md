# Lambdas and Streams lab: a shop's morning reports

You finish the morning reports of a small online shop. The shop's orders are in a database table. Your code loads them into `Order` records, builds rules from lambdas, and turns the list into reports with streams: open orders, a summary line, counts and groups, totals per customer and the largest open order. In the last task you move the finance total into SQL, because the program needs only the totals, not the rows.

You practise lambdas, functional interfaces and their default methods, stream pipelines, collectors, `Optional`, and reducing data in SQL.

The full learner instructions are in [`.evolv/instructions.md`](.evolv/instructions.md), which the lab platform shows as the lab's instructions.

## Layout

This repository follows the Exa coding-lab scaffold for Java.

| Path | What it is |
|---|---|
| `src/main/java/reports/Order.java` | Given record, one row of the orders table |
| `src/main/java/reports/OrderLoader.java` | Lab source (Tasks 1 and 8) |
| `src/main/java/reports/OrderReports.java` | Lab source (Tasks 2-7) |
| `src/main/java/reports/ReportApp.java` | Given demo program |
| `src/test/java/reports/OrderReportsTest.java` | Visible tests |
| `fixtures/orders.sql` | The twelve orders, loaded into an in-memory H2 database |
| `.evolv/instructions.md` | Learner instructions |
| `.evolv/grade.yml` | Grading manifest, used by Run Tests and by the grader |
| `pom.xml` | Maven build: Java 17, JUnit 5.11.3 and the H2 database driver |
| `Dockerfile` | Image for local Run Tests; warms the Maven cache |
| `expected/test-summary.txt` | The result of a finished lab |

There are no held-out tests in this lab: every test is visible.

## Run the tests

On the platform, select **Run Tests**. From a terminal with JDK 17 or later and Apache Maven 3.9:

```
mvn -q test
```

All 8 tests fail until the `TODO`s are done. That is expected.
No database server is needed: the tests create an H2 database in memory.
