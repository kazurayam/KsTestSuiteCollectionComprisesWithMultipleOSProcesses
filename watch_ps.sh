while true; do ps -A | grep Katalon | grep -v grep | cut -c 1-110; 
echo
sleep 5
done
