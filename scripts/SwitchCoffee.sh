#!/bin/bash
cd /home/pi/RPi_Relay_Board/shell
sudo ./Relay.sh CH1 ON > /dev/null 2>&1
sudo ./Relay.sh CH1 OFF > /dev/null 2>&1
