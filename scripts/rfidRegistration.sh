#!/bin/bash
#            // script should:
#            // 1. accept one argument containing the uuid
#            // 2. retrieve the rfid
#            // 3. create a file in the rfid_map directory named as the uuid,
#            //    containing a line with format <uuid>:<retreived rfid>
#            // 4. return exit status 0 if all is ok.

registrationModeStatusFile=/home/pi/Documents/Java/CryptoCoffee/scripts/REGISTRATION_MODE

while true
do
  if [ ! -f registrationModeStatusFile ]
  then
     echo "READER MODE!!!!!"
     echo "Waiting for tag or device..."
     rfid=$(explorenfc-basic | grep "ISO14443A UID" | cut -b17- | sed "s/
//g")
     echo "$rfid" > /tmp/rfid.tmp.reader
     scp /tmp/rfid.tmp.reader pi@192.168.0.33:/tmp/rfid.tmp.reader
  else
     if [ -f /tmp/rfid.tmp ]
     then
       echo "Waiting for website to pickup"
       chown www-data. /tmp/rfid.tmp
       sleep 10
       continue
     fi
     echo "REGISTER MODE!!!!!"
     echo "Waiting for tag or device..."
     rfid=$(explorenfc-basic | grep "ISO14443A UID" | cut -b17- | sed "s/
//g")
     echo "$rfid" > /tmp/rfid.tmp
     chown www-data. /tmp/rfid.tmp
   fi
done