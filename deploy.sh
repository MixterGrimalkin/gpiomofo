#!/usr/bin/env bash
ip="192.168.1.69"
echo
if [ "$1" = "-clean" ]
then
    echo "Removing Existing Application..."
    sshpass -p raspberry ssh pi@${ip} "cd /home/pi; rm -r gpiomofo"
fi
if [ "$1" != "-skipjava" ]
then
    echo "Compiling Java..."
    mvn clean package
fi
echo "Uploading Application..."
sshpass -p raspberry scp -r target/gpiomofo pi@${ip}:
sshpass -p raspberry ssh pi@${ip} "cd /home/pi/gpiomofo; sudo chmod +x *.sh"
echo
echo "Done"
echo