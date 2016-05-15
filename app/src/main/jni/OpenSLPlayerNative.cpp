#include "OpenSLPlayerNative.h"

#include <stdlib.h>

OpenSLPlayerNative* OpenSLPlayerNative::_instance = NULL;

OpenSLPlayerNative::OpenSLPlayerNative() {

}

OpenSLPlayerNative *OpenSLPlayerNative::getInstance() {
    if (_instance == NULL)
        _instance = new OpenSLPlayerNative();

    return _instance;
}

bool OpenSLPlayerNative::init() {
    return true;
}
