#!/usr/bin/env bash
ip=192.168.1.69
if [ "$1" = "" ]
then
    echo "No Trigger specified"
    exit 1
fi
if [ "$2" = "off" ]
then
    curl -d "" http://${ip}:8001/gpiomofo/trigger/$1/cancel
else
    curl -d "" http://${ip}:8001/gpiomofo/trigger/$1/fire
fi
echo