#!/bin/bash
#            // script should:
#            // 1. accept one argument containing the uuid
#            // 2. retrieve the rfid
#            // 3. create a file in the rfid_map directory named as the uuid,
#            //    containing a line with format <uuid>:<retreived rfid>
#            // 4. return exit status 0 if all is ok.

set -x

registrationModeStatusFile=/home/pi/Documents/Java/CryptoCoffee/scripts/REGISTRATION_MODE
apiServerIP=localhost
apiServerPort=8088

while true
do
  if [ ! -f $registrationModeStatusFile ]
  then
     echo "READER MODE!!!!!"
     echo "Waiting for tag or device..."
     rfid=$(explorenfc-basic | grep "ISO14443A UID" | cut -b17- )
     echo "Received Rfid ${rfid}"
     echo "Order coffee for ${rfid}"
     #curl http://${apiServerIP}:${apiServerPort}/api/cryptoCoffee/transaction/${rfid}
   else
     echo "REGISTRATION MODE!!!"
     sleep 2
   fi
done
