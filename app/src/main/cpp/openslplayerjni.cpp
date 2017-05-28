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
Java_kurufasulye_synthesizer_OpenSLPlayer_init(JNIEnv *env, jobject instance, jint sampleRate, jint bufferSize) {
    jboolean retVal;

    OpenSLPlayerNative *openSLPlayerNative = OpenSLPlayerNative::getInstance();
    retVal = (jboolean)openSLPlayerNative->init(sampleRate, bufferSize);

    return retVal;
}

#ifdef __cplusplus
};
#endif