#!/usr/bin/env bash
export MIDIDEVICE="USB Uno MIDI Interface"
aconnect "$MIDIDEVICE" "Midi Through" 2>/dev/null
sudo java -Djava.library.path=/home/pi/gpiomofo/c -jar gpiomofo.jar $*
aconnect -d "$MIDIDEVICE" "Midi Through" 2>/dev/null
