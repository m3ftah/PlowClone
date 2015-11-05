/* 
 * Author : Meftah Lakhdar.
 * SmartPlow Arduino code
 * Checking the Plow state and reporting on change by "O" or "1" 
 * Respond: 
 * to "2" by vibrating,
 * to "3" by reporting the Plow state,
 * else doing nothing.
 */

#include "SoftwareSerial.h"
#include "Time.h"
const int TX_BT=10;
const int RX_BT=11;
const char STATE_ON = '0', STATE_OFF = '1', HEAD_ON = '3', HEAD_OFF = '4', ALARM_ALERT = 'x',ALARM_START = 'a';

SoftwareSerial btSerial(TX_BT,RX_BT);

int plow = 3, state = 0, waiting = 1000;
byte vibroNum = 25000, vibrorPin = 12;
unsigned long timeout1 = 3000, timeref1 = 0;

struct Timing{
  byte h1;
  byte m1;
  byte h2;
  byte m2;
};
Timing timings[20] = {};
Timing timeTemp = {};
byte index = 0;
void setup() {
    // sets the pins mode
    pinMode(vibrorPin,OUTPUT);    
    pinMode(plow,INPUT);
    
    digitalWrite(vibrorPin, LOW);
    
    btSerial.begin(9600); // Default connection rate for my BT module
}

void loop() {
  //check the Plow state every 'timeout1' ms
  if (millis() - timeref1 > timeout1){
    timeref1 = millis();
    if (digitalRead(plow) != state){
      if (digitalRead(plow)){
          timeTemp.h1 = hour();
          timeTemp.m1 = minute();
      }else{
          timeTemp.h2 = hour();
          timeTemp.m2 = minute();
          addTime();
      }
      state = !state;
      repportChange();
    }
  }
  //if some data received, read it and proceed with the orders.
  if(btSerial.available() > 0){
    String str = "";
    char btRead = btSerial.read();
    switch(btRead){
      case '2' : 
                if (digitalRead(plow)){
                  vibror();
                  btSerial.print(ALARM_START);
                }else{
                  btSerial.print(ALARM_ALERT);
                }
        break;
      case 'g' : // Send the state of the Plow
                repport();
        break;
      case 't' ://Get Time from Android phone/Teblet
                str="";
                delay(10);
                while (btSerial.available() > 0){
                  delay(10);
                  str += (char)btSerial.read();
                }
                time(str);
       break;
      case 's' :
                btSerial.print(getStats());
       break;
      case 'v' :
                str="";
                delay(10);
                while (btSerial.available() > 0){
                  delay(10);
                  str += (char)btSerial.read();
                }
                vibroNum = str.toInt()*1000;
       break;
    }
  }
}
/*
 * Vibrate 'num' times for 'waiting'ms
 */
void vibror(){
  byte i = 0;
  unsigned long ref = millis();
  while (digitalRead(plow) && millis() - ref > vibroNum){
    digitalWrite(vibrorPin,1);
    delay(waiting);
    digitalWrite(vibrorPin,0);
    delay(waiting);
  }
}
/*
 * Repport to bluetooth the state of plow on demand
 */
void repport(){
  if (digitalRead(plow)) 
    btSerial.print(HEAD_ON);
  else 
    btSerial.print(HEAD_OFF);
}
/*
 * Repport to bluetooth the state of plow in case of change
 */
void repportChange(){
  if (digitalRead(plow)) 
    btSerial.print(STATE_ON);
  else 
    btSerial.print(STATE_OFF);
}
/*
 * Set Time from a string
 */
void time(String str){
    int hour = str.substring(0,2).toInt();
    int minute = str.substring(3,5).toInt();
    setTime(hour,minute,0,10,11,15);
}
/*
 * Utilities for the table
 */
void addTime(){
  timings[index] = timeTemp;
  index++;
}
/*
 * Gets stats from timings table
 */
String getStats(){
  String stats = "";
  int i = 0;
  for (i=0;i<index;i++){
    stats +=timings[i].h1;
    stats +=":";
    stats +=timings[i].m1;
    stats += ",";
    stats +=timings[i].h2;
    stats +=":";
    stats +=timings[i].m2;
    stats +=";";
  }
  index = 0;
  return stats;
  
}

