#!/bin/bash

# start_app-etl.sh
# control script to launch the app-etl
# author: tom geary 13/6/2020

set -e

instructions() {
        echo "$0 [debug]"
        echo "eg. $0 debug"
}

## entry ##

debug=$1
echo "starting app-etl $1"


if [[ $# -gt 1 ]]; then
        instructions
        exit 1
fi

if [ -z $debug ]; then
        java -Xmx512M -jar /app-etl-*.jar
elif [ ! -z $debug ]; then
        java -Xdebug \
		-Xrunjdwp:transport=dt_socket,server=y,address=19002,suspend=n \
	        -Dlogging.level.tom.ff.etl=DEBUG -jar /app-etl-*.jar 
else
        instructions $0; exit 1
fi
