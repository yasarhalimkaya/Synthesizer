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

    const char* TAG;
    static OpenSLPlayerNative* _instance;
    SLObjectItf engineObject;
    SLEngineItf engineEngine;
    SLObjectItf outputMixObject;

public:
    static OpenSLPlayerNative* getInstance();
    bool init(int sampleRate, int bufferSize);

};

#endif //SYNTHESIZER_OPENSLPLAYERNATIVE_H
