//
// Created by Ferfoui on 23/01/2025.
//

#include <Arduino.h>
#include "SerialCommunication.hpp"
#include "Lights.hpp"

#define LEDS_GPIO {LED_BUILTIN, 28, 27, 26, 22, 21, 20, 19, 18, 17, 16}

SerialCommunication serial;
Lights lights(LEDS_GPIO);

void setup()
{
    serial.init(SerialUSB);
}

void loop()
{
    if (SerialUSB.available())
    {
        const String input = SerialUSB.readString();
        SerialUSB.println(input);

        if (input.equals("H"))
        {
            lights.setLedState(0, HIGH);
        }
        else if (input.equals("L"))
        {
            lights.setLedState(0, LOW);
        }
    }
}
