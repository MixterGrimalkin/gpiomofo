#!/usr/bin/env bash
if [ "$1" = "" ]
then
    echo "Please specify a Pi!"
    exit 1
elif [ "$1" = "local" ]
then
    ip="127.0.0.1"
else
    ip="192.168.0.$1"
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