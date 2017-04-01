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

bool currentFrame[3][32][192];
bool nextFrame[3][32][192];

int clockPin = 0;
int store = 1;
int output = 2;
int data1R = 3;
int data2R = 4;
int data1G = 5;
int data2G = 6;
int addr0 = 21;
int addr1 = 22;
int addr2 = 23;
int addr3 = 24;


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

void sendSerialString(bool red1[], bool green1[], bool red2[], bool green2[]) {
    int col;
    for (col = 0; col < cols ; col++) {
        delayMicroseconds(CLOCK_DELAY);
        digitalWrite(clockPin, LOW);
        digitalWrite(data1R, !red1[col] );
        digitalWrite(data1G, !green1[col] );
        digitalWrite(data2R, !red2[col] );
        digitalWrite(data2G, !green2[col] );
        delayMicroseconds(CLOCK_DELAY);
        digitalWrite(clockPin, HIGH);
    }
}

void decodeRowAddress(int row) {
    digitalWrite(addr0, CHECK_BIT(row, 0)!=0);
    digitalWrite(addr1, CHECK_BIT(row, 1)!=0);
    digitalWrite(addr2, CHECK_BIT(row, 2)!=0);
    digitalWrite(addr3, CHECK_BIT(row, 3)!=0);
}

void push() {
    if ( !paused ) {
        int row;
        for (row = 0; row < rows/2; row++) {
            sendSerialString(currentFrame[0][row], currentFrame[1][row], currentFrame[0][row + rows / 2], currentFrame[1][row + rows / 2]);
            digitalWrite(store, HIGH);
            digitalWrite(output, HIGH);
            delayMicroseconds(CLOCK_DELAY*50);
            decodeRowAddress(row);
            delayMicroseconds(CLOCK_DELAY*50);
            digitalWrite(output, LOW);
            digitalWrite(store, LOW);
        }
    }
}

void init(int r, int c) {

    rows = r;
    cols = c;


    printf("Starting C RaspPi LightBoard....\n");
    printf("Raspberry Pi revision: %d\n", piBoardRev());
    printf("%dx%d\n", rows, cols);

    pushTestPattern();

    wiringPiSetup() ;

//    piHiPri(99);

    pinMode(clockPin, OUTPUT);
    pinMode(store, OUTPUT);
    pinMode(output, OUTPUT);
    pinMode(data1R, OUTPUT);
    pinMode(data2R, OUTPUT);
    pinMode(data1G, OUTPUT);
    pinMode(data2G, OUTPUT);
    pinMode(addr0, OUTPUT);
    pinMode(addr1, OUTPUT);
    pinMode(addr2, OUTPUT);
    pinMode(addr3, OUTPUT);

    printf("Board Running\n");

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
        nextFrame[0][(int)r][(int)c] = !(bool)red;
        nextFrame[1][(int)r][(int)c] = !(bool)green;
        if ( r==rows-1 && c==cols-1 ) {
            paused = true;
            int r2 = 0;
            int c2 = 0;
            for ( r2=0; r2<rows; r2++ ) {
                for ( c2=0; c2<cols; c2++ ) {
                    currentFrame[0][r2][c2] = nextFrame[0][r2][c2];
                    currentFrame[1][r2][c2] = nextFrame[1][r2][c2];
                }
            }
            paused = false;
        }
    }
  }

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_sleep
  (JNIEnv *env, jobject o) {
    paused = true;
    clearBoard();
  }

JNIEXPORT void JNICALL Java_net_amarantha_gpiomofo_display_lightboard_NativeLightBoard_wake
  (JNIEnv *env, jobject o) {
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
    init(32, 192);
    return 0;
}
