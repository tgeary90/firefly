#!/bin/bash

./gradlew :app-agent:bootRun &
./gradlew :app-etl:bootRun &
