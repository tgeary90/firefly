#!/bin/bash

if [[ `docker ps|grep -c ' ff_es' > 0` ]]; then
    exit 0
elif [[ `docker ps -a|grep -c ' es' ` > 0 ]]; then
    docker rm ff_es
else
    echo no elastic container, creating...
fi
docker run -d --name ff_es -p 9200:9200 -p 9300:9300 elasticsearch:6.4.3

