# JavaZone Presentation Project - 2024

## Test better and more with less friction!

[![Main build and test](https://github.com/atomfinger/JavaZone-2024/actions/workflows/gradle.yml/badge.svg)](https://github.com/atomfinger/JavaZone-2024/actions/workflows/gradle.yml)

### Description

#### Package Overview

- Web: Contains the web API
- Service: Contains "Domain logic" and ties the system together
- Persistence: Contains everything related to databases, including database entities
- Integration: The code related to integrating with other web services
- External_orderservice: Would normally not be here. Just exists to generate contract test stubs.

#### Overall architecture

```mermaid
flowchart TD
    A["Frontend"]
	A ---> n2["BookService"]
	n2
	n2 ---> n1[("Database")]
	n2 --- n3["BestReads"]
	n2 --- n4["InventoryService"]
	n2 --- n5["OrderService"]
```

In this fictional system we have multiple dependencies:

- BookService: This service :)
- InventoryService: Responsible to keep track of inventory - I.e. whether we have a book ready for sale or whether we need to order new copies.
- OrderService: Keeps track of the book orders.
- BestReads: A fictional third-party service that has an API to provide review scores.
- Database: The BookService's SQL database

#### Test related tools used in the project

- [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract): Used to write contracts and generate
  contract tests.
- [TestContainers](https://testcontainers.com/): Used to spin up dependencies, such as Postgresql.
- [ApprovalTests](https://approvaltests.com/): Simplifies validating returns from APIs - especially for larger responses.
- [AssertJ](https://assertj.github.io/doc/): More expressive and easier asserts.
- [MockServer](https://mock-server.com/): Makes it easy to mock calls to external service.


### Requirements

- Java 21+
- Gradle
- [Docker-API compatible container runtime](https://java.testcontainers.org/supported_docker_environment/)

### Build

'gradle build'

### Running tests

'gradle test'
