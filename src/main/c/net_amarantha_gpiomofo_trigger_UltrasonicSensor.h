/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_amarantha_gpiomofo_trigger_UltrasonicSensor */

#ifndef _Included_net_amarantha_gpiomofo_trigger_UltrasonicSensor
#define _Included_net_amarantha_gpiomofo_trigger_UltrasonicSensor
#ifdef __cplusplus
extern "C" {
#endif
#undef net_amarantha_gpiomofo_trigger_UltrasonicSensor_SAMPLES
#define net_amarantha_gpiomofo_trigger_UltrasonicSensor_SAMPLES 7L
#undef net_amarantha_gpiomofo_trigger_UltrasonicSensor_MIN_VALUE
#define net_amarantha_gpiomofo_trigger_UltrasonicSensor_MIN_VALUE 250L
#undef net_amarantha_gpiomofo_trigger_UltrasonicSensor_MAX_VALUE
#define net_amarantha_gpiomofo_trigger_UltrasonicSensor_MAX_VALUE 2200L
/*
 * Class:     net_amarantha_gpiomofo_trigger_UltrasonicSensor
 * Method:    init
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_trigger_UltrasonicSensor_init
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     net_amarantha_gpiomofo_trigger_UltrasonicSensor
 * Method:    measure
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_net_amarantha_gpiomofo_trigger_UltrasonicSensor_measure
  (JNIEnv *, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
