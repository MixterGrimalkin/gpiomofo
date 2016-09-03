#!/usr/bin/env bash
if [ "$1" = "" ]
then
    echo "Please specify a Pi!"
    exit 1
else
    ip=`./piaddress.sh $1`
fi
if [ "$2" = "" ]
then
    echo "No Trigger specified"
    exit 1
fi
if [ "$3" = "off" ]
then
    curl -d "" http://${ip}:8001/gpiomofo/trigger/$2/cancel
else
    curl -d "" http://${ip}:8001/gpiomofo/trigger/$2/fire
fi
echo