#!/bin/bash

exec 2>/dev/null

pidETL=`jps|grep ETLApplication|cut -d' ' -f1`
kill $pidETL

pidAgent=`jps|grep AgentApplication|cut -d' ' -f1`
kill $pidAgent

if [[ `ps -aux|grep -c $pidETL` -gt 0 ]]; then
	echo "ETLApplication killed"
else 
	echo "ETLApplication not killed"
	exit 1
fi

if [[ `ps -aux|grep -c $pidAgent` -gt 0 ]]; then
	echo "AgentApplication killed"
else 
	echo "AgentApplication not killed"
	exit 1
fi

