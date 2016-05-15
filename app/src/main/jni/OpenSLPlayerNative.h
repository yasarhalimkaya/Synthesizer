/**
 *
 */
#ifndef SYNTHESIZER_OPENSLPLAYERNATIVE_H
#define SYNTHESIZER_OPENSLPLAYERNATIVE_H

class OpenSLPlayerNative {
private:
    OpenSLPlayerNative();
    static OpenSLPlayerNative* _instance;

public:
    static OpenSLPlayerNative* getInstance();
    bool init();

};

#endif //SYNTHESIZER_OPENSLPLAYERNATIVE_H
