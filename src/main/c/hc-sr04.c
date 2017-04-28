#include <stdio.h>
#include <unistd.h>
//#include <stdlib.h>
#include <wiringPi.h>
#include <time.h>
#include <jni.h>
#include <stdbool.h>
#include "net_amarantha_gpiomofo_service_gpio_ultrasonic_RangeSensorHCSR04.h"

int TIMEOUT = 10000000;

bool initialised = false;

void init(int triggerPin, int echoPin) {
    printf("Setting up native HC-SR04...\n");
    if ( !initialised ) {
        wiringPiSetup() ;
        initialised = true;
    }
    pinMode(triggerPin, OUTPUT);
    pinMode(echoPin, INPUT);
    digitalWrite(triggerPin, LOW);
    delay(1000);
}

long lastTravelTime = 0;

long measure(int triggerPin, int echoPin) {

    digitalWrite(triggerPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(triggerPin, LOW);

    long startTime = 0;
    while((digitalRead(echoPin) == LOW)) { // && (micros() - startTime) <= TIMEOUT) {
        startTime = micros();
    }

    long endTime = 0;
    while((digitalRead(echoPin) == HIGH)) { // && (micros() - endTime) <= TIMEOUT) {
        endTime = micros();
    }
    long travelTime = endTime - startTime;

    if ( travelTime<=0 ) {
        printf("\nResetting....\n");
//        delay(1000);
        pinMode(triggerPin, OUTPUT);
        digitalWrite(triggerPin, LOW);
        pinMode(echoPin, OUTPUT);
        digitalWrite(echoPin, LOW);
        pinMode(echoPin, INPUT);
//        delay(1000);
    }

    return travelTime;

}

int main(int argc, char **argv) {

    if ( argc==3 ) {

        int trigger = atoi(argv[1]);
        int echo = atoi(argv[2]);

        printf("Trigger %d, Echo %d\n", trigger, echo);

        init(trigger, echo);

        printf("Starting sensor loop...\n");

        for ( ;; ) {
            printf("time=");
            printf("%d", measure(trigger, echo));
            printf("\n");
            delay(100);
        }
    }

}

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_service_gpio_ultrasonic_RangeSensorHCSR04_init
  (JNIEnv * env, jobject o, jint trigger, jint echo) {
    init(trigger, echo);
  }


JNIEXPORT jlong JNICALL Java_net_amarantha_gpiomofo_service_gpio_ultrasonic_RangeSensorHCSR04_measure
  (JNIEnv * env, jobject o, jint trigger, jint echo) {
    return measure(trigger, echo);
  }
