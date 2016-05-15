/**
 * Implements the native jni functions defined in OpenSLPlayer.java
 * Delegates actual implementations to classes as much as possible
 */
#include <jni.h>

#include "OpenSLPlayerNative.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL
Java_kurufasulye_synthesizer_OpenSLPlayer_init(JNIEnv *env, jobject instance) {
    jboolean retVal;

    OpenSLPlayerNative *openSLPlayerNative = OpenSLPlayerNative::getInstance();
    retVal = openSLPlayerNative->init() ? 1 : 0;

    return retVal;
}

#ifdef __cplusplus
};
#endif