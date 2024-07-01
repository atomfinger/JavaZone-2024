# JavaZone Presentation Project - 2024

## Test better and more with less friction!

[![Main build and test](https://github.com/atomfinger/JavaZone-2024/actions/workflows/gradle.yml/badge.svg)](https://github.com/atomfinger/JavaZone-2024/actions/workflows/gradle.yml)

### Description

#### Package Overview

- Web: Contains the web API
- Service: Contains "Domain logic" and ties the system together
- Persistence: Contains everything related to databases, including database entities

#### Test related tools used in the project

- [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract): Used to write contracts and generate
  contract tests.
- [TestContainers](https://testcontainers.com/): Used to spin up dependencies, such as Postgresql.
- [ApprovalTests](https://approvaltests.com/): Simplifies validating returns from APIs - especially for larger responses.
- [AssertJ](https://assertj.github.io/doc/): More expressive and easier asserts.


### Requirements

- Java 21+
- Gradle
- [Docker-API compatible container runtime](https://java.testcontainers.org/supported_docker_environment/)

### Build

'gradle build'

### Running tests

'gradle test'
