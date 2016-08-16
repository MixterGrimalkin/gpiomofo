#!/usr/bin/env bash
if [ "$1" = "" ]
then
    echo "Please specify a Pi!"
    exit 1
elif [ "$1" = "local" ]
then
    ip="127.0.0.1"
else
    ip="192.168.42.$1"
fi
echo "Deploy GpioMofo to ${ip}..."
if [ "$2" != "-skipjava" ]
then
    echo "Compiling Java..."
    mvn clean package
    if [ ! -f "target/gpiomofo/gpiomofo.jar" ]
    then
        echo "Compilation Failed!"
        exit 1
    fi
fi
if [ "$2" = "-clean" ]
then
    echo "Cleaning Existing Installing..."
    sshpass -p raspberry ssh pi@${ip} "cd /home/pi; rm -r gpiomofo"
    echo "Uploading Application and Libraries..."
    sshpass -p raspberry scp -r target/gpiomofo pi@${ip}:
    echo "Uploading Audio Files..."
    sshpass -p raspberry scp -r audio pi@${ip}:gpiomofo
else
    echo "Uploading Application..."
    sshpass -p raspberry scp src/main/python/*.py pi@${ip}:gpiomofo/python
    sshpass -p raspberry scp src/main/bash/*.sh pi@${ip}:gpiomofo
    sshpass -p raspberry scp target/gpiomofo/*.jar pi@${ip}:gpiomofo
    sshpass -p raspberry scp src/main/c/* pi@${ip}:gpiomofo/c
fi
echo "Activating Scripts..."
sshpass -p raspberry ssh pi@${ip} "cd /home/pi/gpiomofo; sudo chmod +x *.sh; cd c; chmod +x *.sh"
echo "Compiling Native Sources..."
sshpass -p raspberry ssh pi@${ip} "cd /home/pi/gpiomofo/c; ./build.sh"
echo
echo "Done"
echo