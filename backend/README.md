# Firefly - Custom Cloud ETL

## Introduction

Firefly is a little distributed app to load transactions from a custom configured cloud provider and load 
it to a local (or cloud) elastic instance. It is a demo app to demonstrate how this could be done.

## Design Goals

* Model the domain using the Algebraic Type System in scala
* Make the domain model serve as design documentation
* Make the design documentation compilable - so it doesnt get out-of-sync with the implementation
* Make the domain model readable by domain experts

## Architecture

components:
![components](./documentation/architecture.png "architecture")

## Design

domain:
![domain](./documentation/domain.png "domain")

bounded contexts:
![bounded_contexts](./documentation/bounded_contexts.png "bounded contexts")

modules:
![modules](./documentation/packaging.png "modules")

### Workflows:

fetch transactions:
![flows](./documentation/fetch_txns.png "fetch-txns")

run etl:
![flows2](./documentation/run_etl.png "run-etl")

## Implementation Notes
1. model is in model.scala
2. layers are io and domain

## Run
./gradle bootRun

## Test
./gradlew check

## Integration Test
./gradlew integrationTest

## Develop
git clone https://tgeary90@bitbucket.org/tgeary90/firefly.git

