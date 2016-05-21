#include "OpenSLPlayerNative.h"

#include <stdlib.h>
#include <android/log.h>
const char* TAG = "OpenSLPlayerNative";
#define LOG(assertion, message) if(!assertion) __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s : %d -> %s", __FUNCTION__, __LINE__, message);

OpenSLPlayerNative* OpenSLPlayerNative::_instance = NULL;

OpenSLPlayerNative::OpenSLPlayerNative() :
    initialized(false),
    engineObject(NULL),
    engineEngine(NULL),
    outputMixObject(NULL),
    playerObject(NULL),
    playerPlay(NULL),
    playerBufferQueue(NULL),
    recorderObject(NULL),
    recorderRecord(NULL),
    recorderBufferQueue(NULL)
{

}

OpenSLPlayerNative *OpenSLPlayerNative::getInstance() {
    if (_instance == NULL)
        _instance = new OpenSLPlayerNative();

    return _instance;
}

// this callback handler is called every time a buffer finishes playing
void playerBufferQueueCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
{
    LOG(false, "Not implemented");
}

// this callback handler is called every time a buffer finishes recording
void recorderBufferQueueCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
{
    LOG(false, "Not implemented");
}

bool OpenSLPlayerNative::init(int sampleRate, int bufferSize) {
    if (initialized)
        return true;

    SLresult result;

    // Get the engine
    {
        result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
        LOG(result == SL_RESULT_SUCCESS, "Failed to create engine");

        result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
        LOG(result == SL_RESULT_SUCCESS, "Failed to realize engine");

        result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
        LOG(result == SL_RESULT_SUCCESS, "Failed get engine interface");

        // Create output mix
        const SLInterfaceID ids[] = {SL_IID_VOLUME};
        const SLboolean reqs[] = {SL_BOOLEAN_FALSE};
        result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1,
                                                  ids, reqs);
        LOG(result == SL_RESULT_SUCCESS, "Failed to create output mix");

        result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
        LOG(result == SL_RESULT_SUCCESS, "Failed to realize output mix");
    }

    // Create Audio Player
    {
        if (sampleRate >= 0)
            sampleRate = sampleRate * 1000;

        // Create audio source
        SLDataLocator_AndroidSimpleBufferQueue locatorBufferQueue;
        locatorBufferQueue.locatorType = SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE;
        locatorBufferQueue.numBuffers = 2;

        SLDataFormat_PCM formatPcm;
        formatPcm.formatType = SL_DATAFORMAT_PCM;
        formatPcm.numChannels = 1;
        formatPcm.samplesPerSec = SL_SAMPLINGRATE_8;
        formatPcm.bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
        formatPcm.containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
        formatPcm.channelMask = SL_SPEAKER_FRONT_CENTER;
        formatPcm.endianness = SL_BYTEORDER_LITTLEENDIAN;

        // Setting native sample rate will trigger fast audio path
        if (sampleRate)
            formatPcm.samplesPerSec = (SLuint32) sampleRate;

        SLDataSource audioSource = {&locatorBufferQueue, &formatPcm};

        // Create audio sink
        SLDataLocator_OutputMix locatorOutputMix;
        locatorOutputMix.locatorType = SL_DATALOCATOR_OUTPUTMIX;
        locatorOutputMix.outputMix = outputMixObject;

        SLDataSink audioSink = {&locatorOutputMix, NULL};

        // Create audio player
        SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME};
        SLboolean reqs[] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
        result = (*engineEngine)->CreateAudioPlayer(engineEngine, &playerObject, &audioSource,
                                                    &audioSink,
                                                    sampleRate ? 2 : 3, ids,
                                                    reqs);
        LOG(result == SL_RESULT_SUCCESS, "Failed to create audio player");

        result = (*playerObject)->Realize(playerObject, SL_BOOLEAN_FALSE);
        LOG(result == SL_RESULT_SUCCESS, "Failed to realize audio player");

        result = (*playerObject)->GetInterface(playerObject, SL_IID_PLAY, &playerPlay);
        LOG(result == SL_RESULT_SUCCESS, "Failed to get audio player interface");

        result = (*playerObject)->GetInterface(playerObject, SL_IID_BUFFERQUEUE,
                                               &playerBufferQueue);
        LOG(result == SL_RESULT_SUCCESS, "Failed to get audio player buffer queue interface");

        result = (*playerBufferQueue)->RegisterCallback(playerBufferQueue,
                                                        playerBufferQueueCallback,
                                                        NULL);
        LOG(result == SL_RESULT_SUCCESS, "Failed to register buffer queue callback");

        result = (*playerObject)->GetInterface(playerObject, SL_IID_VOLUME, &playerVolume);
        LOG(result == SL_RESULT_SUCCESS, "Failed to get volume interface");

        result = (*playerPlay)->SetPlayState(playerPlay, SL_PLAYSTATE_PLAYING);
        LOG(result == SL_RESULT_SUCCESS, "Failed to start player");
    }

    // Create Audio Recorder
    {
        // Create audio source
        SLDataLocator_IODevice locatorIoDevice;
        locatorIoDevice.locatorType = SL_DATALOCATOR_IODEVICE;
        locatorIoDevice.deviceType = SL_IODEVICE_AUDIOINPUT;
        locatorIoDevice.deviceID = SL_DEFAULTDEVICEID_AUDIOINPUT;
        locatorIoDevice.device = NULL;

        SLDataSource audioSource = {&locatorIoDevice, NULL};

        // Create audio sink
        SLDataLocator_AndroidSimpleBufferQueue locatorBufferQueue;
        locatorBufferQueue.locatorType = SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE;
        locatorBufferQueue.numBuffers = 2;

        SLDataFormat_PCM formatPcm;
        formatPcm.formatType = SL_DATAFORMAT_PCM;
        formatPcm.numChannels = 1;
        formatPcm.samplesPerSec = SL_SAMPLINGRATE_8;
        formatPcm.bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
        formatPcm.containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
        formatPcm.channelMask = SL_SPEAKER_FRONT_CENTER;
        formatPcm.endianness = SL_BYTEORDER_LITTLEENDIAN;

        SLDataSink audioSink = {&locatorBufferQueue, &formatPcm};

        // Create audio recorder
        const SLInterfaceID ids[] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
        const SLboolean reqs[] = {SL_BOOLEAN_TRUE};
        result = (*engineEngine)->CreateAudioRecorder(engineEngine, &recorderObject,
                                                      &audioSource, &audioSink, 1, ids, reqs);
        LOG(result == SL_RESULT_SUCCESS, "Failed to create audio recorder");

        result = (*recorderObject)->Realize(recorderObject, SL_BOOLEAN_FALSE);
        LOG(result == SL_RESULT_SUCCESS, "Failed to realize audio recorder");

        result = (*recorderObject)->GetInterface(recorderObject, SL_IID_RECORD, &recorderRecord);
        LOG(result == SL_RESULT_SUCCESS, "Failed to get audio recorder interface");

        result = (*recorderObject)->GetInterface(recorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &recorderBufferQueue);
        LOG(result == SL_RESULT_SUCCESS, "Failed to get recorder buffer queue interface");

        result = (*recorderBufferQueue)->RegisterCallback(recorderBufferQueue, recorderBufferQueueCallback, NULL);
        LOG(result == SL_RESULT_SUCCESS, "Failed to register recorder buffer queue callback");

        result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_RECORDING);
        LOG(result == SL_RESULT_SUCCESS, "Failed to start recorder");
    }



    LOG(false, "Successfully initialized OpenSLPlayer");

    initialized = true;
    return initialized;
}





















