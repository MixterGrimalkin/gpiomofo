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
if [ "$1" = "-quick" ]
then
    echo "Uploading Application (quick mode)..."
    sshpass -p raspberry scp target/gpiomofo/*.sh pi@${ip}:gpiomofo
    sshpass -p raspberry scp target/gpiomofo/*.jar pi@${ip}:gpiomofo
    sshpass -p raspberry ssh pi@${ip} "cd /home/pi/gpiomofo; sudo chmod +x *.sh"
else
    echo "Uploading Application..."
    sshpass -p raspberry scp -r target/gpiomofo pi@${ip}:
    sshpass -p raspberry ssh pi@${ip} "cd /home/pi/gpiomofo; sudo chmod +x *.sh"
fi
echo
echo "Done"
echo