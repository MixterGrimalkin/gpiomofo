#!/usr/bin/env bash
sudo nice -n -20 java -Djava.library.path=/home/pi/gpiomofo/c -jar gpiomofo.jar $*
