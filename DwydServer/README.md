# Dwyd
Dwyd is a system for monitoring prices on Russian marketplace.

### The software can work with:
- yandex market.

### Description of functionality:
Every 5 minutes the system parses stores by a specified list of articles. After processing the data, information about the task execution status is written to the task_executions table. In case of successful execution, the price is updated for each product in the products table.

Two methods are available for working with the API:
- start
- save

### Local start
If you want to start this project locally, after clone you need enter this command:
```bash
gradle build
gradle run
```

### Stack
* Java 21
* Gradle 8.6
* Spring Framework
* Databases: H2
* JUnit5
* Playwright
* Liquibase