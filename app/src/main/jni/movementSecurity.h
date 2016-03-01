//
// Created by cody on 2016/1/15.
//

#ifndef TEST1_NDKSAMPLE_H
#define TEST1_NDKSAMPLE_H

#include "jni.h"
#include <stdio.h>
#include <string.h>

extern "C" {
JNIEXPORT jstring JNICALL
        Java_com_lowhot_cody_movement_MainActivity_sayHello(JNIEnv *env, jclass type,
                                                          jstring filename);
}

#endif //TEST1_NDKSAMPLE_H

