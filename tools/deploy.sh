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

if [ "$2" = "-clean" ]
then
    read -p "Really remove existing installation, including properties? " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]
    then
        exit 1
    fi
fi

echo "Deploy GpioMofo to ${ip}..."
if [ "$2" != "-skipjava" ]
then
    echo "Compiling Java..."
    cd ..
    echo "WARNING : SKIPPING TESTS BECAUSE OF FUCKING WEIRD THING"
    mvn -Dmaven.test.skip=true clean package
    cd tools
    if [ ! -f "../target/gpiomofo/gpiomofo.jar" ]
    then
        echo "Compilation Failed!"
        exit 1
    fi
fi

if [ "$2" = "-clean" ]
then
    echo "Cleaning Existing Installing..."
    sshpass -p ${password} ssh pi@${ip} "cd /home/pi; rm -r gpiomofo 2>/dev/null"
    echo "Uploading Application and Libraries..."
    sshpass -p ${password} scp -r ../target/gpiomofo pi@${ip}:
    echo "Uploading Audio Files..."
    sshpass -p ${password} scp -r ../audio pi@${ip}:gpiomofo
else
    echo "Uploading Application..."
    sshpass -p ${password} scp ../target/gpiomofo/*.jar pi@${ip}:gpiomofo
    echo "Uploading Web Interfaces..."
    sshpass -p ${password} scp -r ../src/main/html/* pi@${ip}:gpiomofo/html
    echo "Uploading Scripts and Properties..."
    sshpass -p ${password} scp -r ../src/main/bash/* pi@${ip}:gpiomofo
    sshpass -p ${password} scp -r ../target/gpiomofo/scripts pi@${ip}:gpiomofo
    sshpass -p ${password} scp -r ../src/main/python/* pi@${ip}:gpiomofo/python
    sshpass -p ${password} scp ../config/default.properties pi@${ip}:gpiomofo
fi

echo "Activating Scripts..."
sshpass -p ${password} ssh pi@${ip} "cd /home/pi/gpiomofo; sudo chmod +x *.sh; sudo chmod -x *.jar"

echo "Uploading Native Libraries..."
sshpass -p ${password} scp -r ../src/main/c pi@${ip}:gpiomofo
sshpass -p ${password} ssh pi@${ip} "cd /home/pi/gpiomofo/c; chmod +x build.sh; ./build.sh; rm *.c"

echo
echo "Done"
echo