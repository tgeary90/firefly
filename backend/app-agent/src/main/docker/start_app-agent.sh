#!/bin/sh

# start_app-agent.sh
# control script to launch the app-agent
# author: tom geary 13/6/2020

set -e

instructions() 
{
        echo "$0 [debug]"
        echo "eg. $0 debug"
}

## entry ##

debug=$1
echo "starting app-agent $1"


if [[ $# -gt 1 ]]; then
        instructions
        exit 1
fi

if [ -z $debug ]; then
        java -Xmx512M -jar /app-agent-*.jar
elif [ ! -z $debug ]; then
        java -Xdebug \
		-Xrunjdwp:transport=dt_socket,server=y,address=19001,suspend=n \
	        -Dlogging.level.tom.ff.fetch=DEBUG -jar /app-agent-*.jar 
else
        instructions $0; exit 1
fi
