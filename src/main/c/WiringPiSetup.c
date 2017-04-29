#include <wiringPi.h>
#include <jni.h>
#include "net_amarantha_gpiomofo_service_gpio_WiringPiSetup.h"

int main (void) {
    printf("Nothing to see here");
}

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_service_gpio_WiringPiSetup_wiringPiSetup
  (JNIEnv * env, jclass c) {
    wiringPiSetup();
}
