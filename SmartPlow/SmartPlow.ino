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
const int TX_BT=10;
const int RX_BT=11;

SoftwareSerial btSerial(TX_BT,RX_BT);

int plow = 3, state = 0, waiting = 1000;
byte vibroNum = 6, vibrorPin = 12;
unsigned long timeout1 = 3000, timeref1 = 0;

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
      state = !state;
      repport();
    }
  }
  //if some data received, read it, vibrate, report or ignore.
  if(btSerial.available() > 0){
    char btRead = btSerial.read();
    if (btRead == '2'){
      vibror(vibroNum);
    }else if (btRead =='3') {
      repport();
    }
  }
}
/*
 * Vibrate 'num' times for 'waiting'ms
 */
void vibror(byte num){
  byte i = 0;
  for (i=0; i<num; i++){
    digitalWrite(vibrorPin,1);
    delay(waiting);
    digitalWrite(vibrorPin,0);
    delay(waiting);
  }
}
/*
 * Repport to bluetooth by "0" or "1"
 */
void repport(){
  if ( digitalRead(plow)){
      btSerial.print("1");
  }else{
      btSerial.print("0");
  }
}
