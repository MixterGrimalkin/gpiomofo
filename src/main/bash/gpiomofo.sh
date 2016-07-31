#!/usr/bin/env bash
export MIDIDEVICE="USB Uno MIDI Interface"
aconnect "$MIDIDEVICE" "Midi Through"
sudo java -jar gpiomofo.jar $*
aconnect -d "$MIDIDEVICE" "Midi Through"
