#!/bin/bash

# start rabbit mq

if [[ `docker ps -a|grep -c rmq` -eq 1 ]];then
	docker start rmq
else
	docker run -d --hostname rmq --name rmq -p 15672:15672 -p 5672:5672 rabbitmq:3-management
fi
