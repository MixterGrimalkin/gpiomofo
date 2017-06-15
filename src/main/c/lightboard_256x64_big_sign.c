#include <time.h>
#include <stdio.h>
#include <wiringPi.h>
#include <jni.h>
#include <stdlib.h>
#include <stdbool.h>
#include "net_amarantha_gpiomofo_display_lightboard_NativeLightBoard.h"

#define CHECK_BIT(var,pos) ((var) & (1<<(pos)))

int rows = 0;
int cols = 0;

bool currentFrame[3][64][256];
bool nextFrame[3][64][256];

int addr0 = 0;
int addr1 = 2;
int addr2 = 3;
int addr3 = 4;
int data1R = 5;
int data2R = 6;
int data1G = 7;
int data2G = 21;
int clockPin = 22;
int store = 23;
int output = 25;
int data3R = 8;
int data4R = 9;
int data3G = 10;
int data4G = 11;

//int addr0_2 = 26;
//int addr1_2 = 27;
//int addr2_2 = 28;
//int addr3_2 = 29;
//int clockPin_2 = 12;
//int store_2 = 13;
//int output_2 = 14;

bool paused = false;

int CLOCK_DELAY = 1;

void pushTestPattern() {
    int r;
    int c;
    for ( r=0; r<rows; r++ ) {
        for ( c=0; c<cols; c++ ) {
            if ( c%4==0 || r%4==0 ) {
                currentFrame[0][r][c] = false;
                currentFrame[1][r][c] = false;
            } else {
                currentFrame[0][r][c] = true;
                currentFrame[1][r][c] = true;
            }
        }
    }
}

void sendSerialString(bool red1[], bool green1[], bool red2[], bool green2[], bool red3[], bool green3[], bool red4[], bool green4[]) {
    int col;
    for (col = 0; col < cols ; col++) {
        delayMicroseconds(CLOCK_DELAY);
        digitalWrite(clockPin, LOW);
//        digitalWrite(clockPin_2, LOW);
        digitalWrite(data1R, !red1[col] );
        digitalWrite(data1G, !green1[col] );
        digitalWrite(data2R, !red2[col] );
        digitalWrite(data2G, !green2[col] );
        digitalWrite(data3R, !red3[col] );
        digitalWrite(data3G, !green3[col] );
        digitalWrite(data4R, !red4[col] );
        digitalWrite(data4G, !green4[col] );
        delayMicroseconds(CLOCK_DELAY);
        digitalWrite(clockPin, HIGH);
//        digitalWrite(clockPin_2, HIGH);
    }
}

void decodeRowAddress(int row) {
    digitalWrite(addr0, CHECK_BIT(row, 0)!=0);
    digitalWrite(addr1, CHECK_BIT(row, 1)!=0);
    digitalWrite(addr2, CHECK_BIT(row, 2)!=0);
    digitalWrite(addr3, CHECK_BIT(row, 3)!=0);
//    digitalWrite(addr0_2, CHECK_BIT(row, 0)!=0);
//    digitalWrite(addr1_2, CHECK_BIT(row, 1)!=0);
//    digitalWrite(addr2_2, CHECK_BIT(row, 2)!=0);
//    digitalWrite(addr3_2, CHECK_BIT(row, 3)!=0);
}

void push() {
    if ( !paused ) {
        int row;
        for (row = (rows/4)-1; row >=0 ; row--) {
//        for (row = 0; row < rows/4; row++) {
            sendSerialString(currentFrame[0][row],      currentFrame[1][row],
                             currentFrame[0][row + (rows/4)], currentFrame[1][row + (rows/4)],
                             currentFrame[0][row + (rows/2)], currentFrame[1][row + (rows/2)],
                             currentFrame[0][row + ((3*rows)/4)], currentFrame[1][row + ((3*rows)/4)]);
            digitalWrite(store, HIGH);
//            digitalWrite(store_2, HIGH);
            digitalWrite(output, HIGH);
//            digitalWrite(output_2, HIGH);
            delayMicroseconds(CLOCK_DELAY*1);
            decodeRowAddress(row);
            delayMicroseconds(CLOCK_DELAY*1);
            digitalWrite(output, LOW);
//            digitalWrite(output_2, LOW);
            digitalWrite(store, LOW);
//            digitalWrite(store_2, LOW);
        }
    }
}

void init(int r, int c) {

    rows = r;
    cols = c;

    printf("[ - RaspPi native LightBoard %dx%d - ]\n", cols, rows);

    pushTestPattern();

    pinMode(clockPin, OUTPUT);
    pinMode(store, OUTPUT);
    pinMode(output, OUTPUT);
    pinMode(data1R, OUTPUT);
    pinMode(data2R, OUTPUT);
    pinMode(data3R, OUTPUT);
    pinMode(data4R, OUTPUT);
    pinMode(data1G, OUTPUT);
    pinMode(data2G, OUTPUT);
    pinMode(data3G, OUTPUT);
    pinMode(data4G, OUTPUT);
    pinMode(addr0, OUTPUT);
    pinMode(addr1, OUTPUT);
    pinMode(addr2, OUTPUT);
    pinMode(addr3, OUTPUT);

//    pinMode(clockPin_2, OUTPUT);
//    pinMode(store_2, OUTPUT);
//    pinMode(output_2, OUTPUT);
//    pinMode(addr0_2, OUTPUT);
//    pinMode(addr1_2, OUTPUT);
//    pinMode(addr2_2, OUTPUT);
//    pinMode(addr3_2, OUTPUT);

    for ( ;; ) {
        push();
    }

}

void clearBoard() {
    int r,c;
    for ( r=0; r<rows; r++ ) {
        for ( c=0; c<cols; c++ ) {
            currentFrame[0][r][c] = true;
            currentFrame[1][r][c] = true;
        }
    }
    push();
}



/////////
// JNI //
/////////

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_initNative
  (JNIEnv *env, jobject o, jint r, jint c) {
    init(r, c);
  }

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_setPoint
  (JNIEnv *env, jobject o, jint r, jint c, jboolean red, jboolean green) {
    if ( !paused ) {
        currentFrame[0][(int)r][(int)c] = !(bool)red;
        currentFrame[1][(int)r][(int)c] = !(bool)green;
    }
  }

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_sleep
  (JNIEnv *env, jobject o) {
    paused = true;
    clearBoard();
  }

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_wake  (JNIEnv *env, jobject o) {
    paused = false;
  }

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_setPins
  (JNIEnv *env, jobject o, jint clk, jint sto, jint out, jint d1R, jint d2R, jint d1G, jint d2G, jint a0, jint a1, jint a2, jint a3) {
    clockPin = clk;
    store = sto;
    output = out;
    data1R = d1R;
    data2R = d2R;
    data1G = d1G;
    data2G = d2G;
    addr0 = a0;
    addr1 = a1;
    addr2 = a2;
    addr3 = a3;
  }

////////////////
// Test Start //
////////////////

int main (void) {
    printf("Starting LightBoard in Native Mode...\n");
    wiringPiSetup() ;
    init(64, 256);
    return 0;
}
