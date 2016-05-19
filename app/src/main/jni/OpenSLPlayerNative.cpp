#include "OpenSLPlayerNative.h"

#include <stdlib.h>
#include <android/log.h>
#define LOG(assertion, message) if(!assertion) __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s : %d -> %s", __FUNCTION__, __LINE__, message);

OpenSLPlayerNative* OpenSLPlayerNative::_instance = NULL;

OpenSLPlayerNative::OpenSLPlayerNative() :
    engineObject(NULL),
    engineEngine(NULL),
    outputMixObject(NULL),
    TAG("OpenSLPlayerNative")
{

}

OpenSLPlayerNative *OpenSLPlayerNative::getInstance() {
    if (_instance == NULL)
        _instance = new OpenSLPlayerNative();

    return _instance;
}

bool OpenSLPlayerNative::init(int sampleRate, int bufferSize) {
    // Get the engine
    SLresult result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    LOG(result == SL_RESULT_SUCCESS, "Failed to create engine");

    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    LOG(result == SL_RESULT_SUCCESS, "Failed to realize engine");

    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    LOG(result == SL_RESULT_SUCCESS, "Failed get engine interface");

    // Create output mix
    const SLInterfaceID ids[] = {SL_IID_VOLUME};
    const SLboolean req[] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
    LOG(result == SL_RESULT_SUCCESS, "Failed to create output mix");

    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    LOG(result == SL_RESULT_SUCCESS, "Failed to realize output mix");

    // Create Audio Player

    LOG(false, "Successfully initialized OpenSLPlayer");
    return true;
}
