//
// Created by Ferfoui on 05/02/2025.
//

#ifndef PICO_CONTROL_SERIALCOMMUNICATION_HPP
#define PICO_CONTROL_SERIALCOMMUNICATION_HPP

#include "Arduino.h"

class SerialCommunication {
public:
    explicit SerialCommunication();
    void init(UART serial);
    bool available();

private:
    UART* m_pSerial;
};


#endif //PICO_CONTROL_SERIALCOMMUNICATION_HPP
