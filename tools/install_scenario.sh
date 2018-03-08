#!/usr/bin/env bash

if [ ! -f "password" ]
then
    echo "No password set"
    exit 1
else
    password=`cat password`
fi

if [ "$1" = "" ]
then
    echo "Please specify a Pi!"
    exit 1
fi
ip=`./piaddress.sh $1`

if [ "$2" = "" ]
then
    echo "Please specify a Scenario (without .yaml extension)"
    exit 1
fi
filename="$2.yaml"
echo "Uploading ${filename}..."
sshpass -p ${password} scp ../scenarios/${filename} pi@${ip}:gpiomofo/scenarios