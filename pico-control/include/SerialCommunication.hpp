//
// Created by Ferfoui on 05/02/2025.
//

#ifndef PICO_CONTROL_SERIALCOMMUNICATION_HPP
#define PICO_CONTROL_SERIALCOMMUNICATION_HPP

#include "Arduino.h"
#include "Command.hpp"

class SerialCommunication {
public:
    explicit SerialCommunication();
    void init(UART serial);
    bool available();
    Command readCommand();
    void send(String message);

private:
    UART* m_pSerial;
};


#endif //PICO_CONTROL_SERIALCOMMUNICATION_HPP
