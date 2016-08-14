#include <stdio.h>
//#include <stdlib.h>
#include <wiringPi.h>
#include <time.h>
#include <jni.h>
#include "net_amarantha_gpiomofo_trigger_UltrasonicSensor.h"

int TRIG = 0;
int ECHO = 5;

int TIMEOUT = 10000000;

void init() {
    printf("Setting up native HC-SR04...\n");
    wiringPiSetup() ;
    pinMode(TRIG, OUTPUT);
    pinMode(ECHO, INPUT);
    digitalWrite(TRIG, LOW);
    delay(1000);
}

long lastTravelTime = 0;

long measure() {

    digitalWrite(TRIG, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIG, LOW);

    long startTime = 0;
    while((digitalRead(ECHO) == LOW) && (micros() - startTime) <= TIMEOUT) {
        startTime = micros();
    }

    long endTime = 0;
    while((digitalRead(ECHO) == HIGH) && (micros() - endTime) <= TIMEOUT) {
        endTime = micros();
    }
    long travelTime = endTime - startTime;

    if ( travelTime==0 ) {
        wiringPiSetup() ;
        pinMode(TRIG, OUTPUT);
        pinMode(ECHO, INPUT);
        return lastTravelTime;
    }

    lastTravelTime = travelTime;

    return travelTime;

}

int main(void) {

    init();

    for ( ;; ) {
        printf("time=");
        printf("%d",measure());
        printf("\n");
        delay(100);
    }

}

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_trigger_UltrasonicSensor_init
  (JNIEnv * env, jobject o) {
    init();
  }


JNIEXPORT jlong JNICALL Java_net_amarantha_gpiomofo_trigger_UltrasonicSensor_measure
  (JNIEnv * env, jobject o) {
    return measure();
  }
