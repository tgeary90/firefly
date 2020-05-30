#!/bin/bash

if [[ `docker ps -a|grep -c ' ff_es'` -gt 0 ]]; then
    docker start ff_es
else
    echo no elastic container, creating...
    docker run -d --name ff_es -p 9200:9200 -p 9300:9300 elasticsearch:6.4.3
fi

