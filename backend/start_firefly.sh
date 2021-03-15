#!/bin/bash

# start_firefly.sh
# control script to launch the firefly app

./start_rmq.sh
./start_elastic.sh

if [[ "$1" = "app-agent"  ]]; then
    ./gradlew :app-agent:bootRun &
elif [[ "$1" = "app-etl" ]]; then
    ./gradlew :app-etl:bootRun &
else
    ./gradlew :app-agent:bootRun &
    ./gradlew :app-etl:bootRun &
fi
