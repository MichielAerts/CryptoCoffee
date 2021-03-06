
if [ "$1" = "readermode" ]
then
  touch /tmp/READERMODE
else
  [ -f /tmp/READERMODE ] && rm -f /tmp/READERMODE
fi

get_answer()
{
  text=$1
  a=""
  while [ "$a" = "" ]
  do
    printf "$text"
    read -i a
    if [ -n "$a" ]
    then 
      break
    fi
  done
  echo "$a"
}

#trap '' 2  
while true
do
  if [ -f /tmp/READERMODE ]
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
