#!/usr/bin/env bash

if [ ! -f "settings.yaml" ]
then
    cp default-settings.yaml settings.yaml
fi

if [ "$1" = "-simulation" ] || [ "$1" = "-sim" ]
then
    java -cp gpiomofo.jar net.amarantha.gpiomofo.Simulation $*
else
    sudo nice -n -20 java -Djava.library.path=/home/pi/gpiomofo/c -jar gpiomofo.jar $*
fi
