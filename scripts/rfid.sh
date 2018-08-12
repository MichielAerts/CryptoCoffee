#!/bin/bash
# script should:
# 1. accept one argument containing the uuid
# 2. retrieve the rfid
# 3. create a file in the rfid_map directory named as the uuid,
#    containing a line with format <uuid>:<retreived rfid>
# 4. return exit status 0 if all is ok.

set -x

if [ $# -ne 1 ]
then
  echo "We expect one input argument, the uuid for the call"
  return 1
fi

uuid=$1
rfidStorageMap=/home/pi/Documents/Java/CryptoCoffee/rfids/

echo "REGISTRATION MODE!!!!!"
echo "Waiting for tag or device..."
rfid=$(explorenfc-basic | grep "ISO14443A UID" | cut -b17- | sed "s///g")
echo "Received Rfid ${rfid}"
echo "create file ${rfidStorageMap}${uuid} with line ${uuid}:${rfid}"
echo ${uuid}:${rfid} >> ${rfidStorageMap}${uuid}
