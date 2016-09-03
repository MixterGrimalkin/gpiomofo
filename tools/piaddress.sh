#!/usr/bin/env bash
if [ "$1" = "local" ]
then
    echo "127.0.0.1"
    exit 0
fi
if [ "$1" = "set" ]
then
    if [ "$2" = "" ]
    then
        echo "Usage:"
        echo "  piaddress.sh set <IP-STUB>"
        exit 1
    fi
    echo $2>ipstub
    echo "IP Stub set"
    exit 0
fi
if [ -f "ipstub" ]
then
    if [ "$1" = "clear" ]
    then
        rm ipstub
        echo "IP Stub cleared"
        exit 0
    else
        echo `cat ipstub`$1
        exit 0
    fi
else
    if [ "$1" = "clear" ]
    then
        echo "No IP Stub set"
        exit 1
    else
        echo $1
        exit 0
    fi
fi