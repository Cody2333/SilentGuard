//
// Created by cody on 2016/1/15.
//

#include "movementSecurity.h"

JNIEXPORT jstring JNICALL Java_com_lowhot_cody_movement_MainActivity_sayHello
        (JNIEnv *env, jclass cls, jstring j_str)
{
    const char *c_str = nullptr;
    char buff[128] = {0};
    jboolean isCopy;
    c_str = env->GetStringUTFChars(j_str, &isCopy);
    printf("isCopy:%d\n",isCopy);
    if(c_str == NULL)
    {
        return NULL;
    }
    printf("C_str: %s \n", c_str);
    sprintf(buff, "hey %s", c_str);
    env->ReleaseStringUTFChars(j_str, c_str);
    return env->NewStringUTF(buff);
}