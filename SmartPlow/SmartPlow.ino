#include "SoftwareSerial.h"
const int TX_BT=10;
const int RX_BT=11;

SoftwareSerial btSerial(TX_BT,RX_BT);
/* SmartPlow Arduino code
**
** Credit: The following example was used as a reference
** Rui Santos: http://randomnerdtutorials.wordpress.com
*/

int ledPin = 13;  // use the built in LED on pin 13 of the Uno
int plow = 3;
int state = 0;
int flag = 0;
byte num = 6, vibrorPin = 12;
int waiting = 1000;

int val=0;// make sure that you return the state only once
unsigned long timeout1 = 3000, timeref1 = 0;
void setup() {
    // sets the pins as outputs:
    pinMode(ledPin, OUTPUT);
    pinMode(12,OUTPUT);    
    pinMode(plow,INPUT);
    digitalWrite(ledPin, LOW);
    digitalWrite(12, LOW);
    
    btSerial.begin(9600); // Default connection rate for my BT module
    Serial.begin(115200);
}

void loop() {
  
    if (millis() - timeref1 > timeout1){
      timeref1 = millis();
      if (digitalRead(plow) != state) repport();
    }
    //if some data is sent, read it and save it in the state variable
    if(btSerial.available() > 0){
      char btRead = btSerial.read();
      if (btRead == '2'){
        vibror(6);
      }else if (btRead =='3') {
        repport();
      }
    }
}
void vibror(byte num){
  byte i = 0;
  for (i=0; i<num; i++){
    digitalWrite(vibrorPin,1);
    delay(waiting);
    digitalWrite(vibrorPin,0);
    delay(waiting);
  }
}
void repport(){
  if ( digitalRead(plow)){
      btSerial.print("1");
  }else{
      btSerial.print("0");
  }
}

