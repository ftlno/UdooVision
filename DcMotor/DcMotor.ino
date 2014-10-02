#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_DCMotor *right = AFMS.getMotor(1);
Adafruit_DCMotor *left = AFMS.getMotor(3);

boolean received = false;

void setup() {
  Serial.begin(230400);
  AFMS.begin();
  stop();
}

void speed(int l, int r){
  left->setSpeed(l);
  left->run(FORWARD);
  right->setSpeed(r);
  right->run(FORWARD);
}

void stop(){
  left->setSpeed(1);
  right->setSpeed(1);
  left->run(FORWARD);
  right->run(FORWARD);
}

void loop() {
  int incoming = 0;
  
  while(Serial.available() > 0){
    incoming = Serial.read();
    received = true;
  }
  
  if(received ){
    if(incoming == 1){
      speed(0,50);
    }else if(incoming == 2){
      speed(50,0);
    }else if(incoming == 3){
      speed(50,50);
    }else{
      stop();
    }
  }
}

