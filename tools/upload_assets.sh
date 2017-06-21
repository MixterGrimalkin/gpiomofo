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

echo "Uploading Images..."
sshpass -p ${password} scp  -r ../images/ pi@${ip}:gpiomofo
echo "Uploading Fonts..."
sshpass -p ${password} scp  -r ../fonts/ pi@${ip}:gpiomofo
echo "Uploading Scenarios..."
sshpass -p ${password} scp  -r ../scenarios/ pi@${ip}:gpiomofo
echo "Uploading Audio..."
sshpass -p ${password} scp  -r ../audio/ pi@${ip}:gpiomofo
echo "Done."