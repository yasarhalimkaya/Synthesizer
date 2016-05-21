/**
 *
 */
#ifndef SYNTHESIZER_OPENSLPLAYERNATIVE_H
#define SYNTHESIZER_OPENSLPLAYERNATIVE_H

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

class OpenSLPlayerNative {
private:
    OpenSLPlayerNative();

    static OpenSLPlayerNative* _instance;
    bool initialized;
    SLObjectItf engineObject;
    SLEngineItf engineEngine;
    SLObjectItf outputMixObject;
    SLObjectItf playerObject;
    SLPlayItf playerPlay;
    SLAndroidSimpleBufferQueueItf playerBufferQueue;
    SLVolumeItf playerVolume;
    SLObjectItf recorderObject;
    SLRecordItf recorderRecord;
    SLAndroidSimpleBufferQueueItf recorderBufferQueue;

public:
    static OpenSLPlayerNative* getInstance();
    bool init(int sampleRate, int bufferSize);

};

#endif //SYNTHESIZER_OPENSLPLAYERNATIVE_H
