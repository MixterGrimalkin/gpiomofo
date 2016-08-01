#!/usr/bin/env bash
ip="192.168.1.69"
echo "Deploy GpioMofo to ${ip}..."
if [ "$1" != "-skipjava" ]
then
    echo "Compiling Java..."
    mvn clean package
    if [ ! -f "target/gpiomofo/gpiomofo.jar" ]
    then
        echo "Compilation Failed!"
        exit 1
    fi
fi
if [ "$1" = "-clean" ]
then
    echo "Cleaning Existing Installing..."
    sshpass -p raspberry ssh pi@${ip} "cd /home/pi; rm -r gpiomofo"
    echo "Uploading Application and Libraries..."
    sshpass -p raspberry scp -r target/gpiomofo pi@${ip}:
    echo "Uploading Audio Files..."
    sshpass -p raspberry scp -r audio pi@${ip}:gpiomofo
else
    echo "Uploading Application..."
    sshpass -p raspberry scp target/gpiomofo/*.sh pi@${ip}:gpiomofo
    sshpass -p raspberry scp target/gpiomofo/*.jar pi@${ip}:gpiomofo
fi
echo "Activating Scripts..."
sshpass -p raspberry ssh pi@${ip} "cd /home/pi/gpiomofo; sudo chmod +x *.sh"
echo
echo "Done"
echo