//
// Created by Ferfoui on 05/02/2025.
//

#include "SerialCommunication.hpp"

void SerialCommunication::init(UART serial) {
    m_pSerial = &serial;
    m_pSerial->begin(9600, SERIAL_8N1);
}

bool SerialCommunication::available() {
    return m_pSerial->available();
}

