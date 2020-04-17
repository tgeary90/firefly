#!/bin/bash

docker run -d --hostname rmq --name rmq -p 15672:15672 -p 5672:5672 rabbitmq:3-management
