//
// Created by Ferfoui on 23/01/2025.
//

#include <Arduino.h>
#include "Lights.hpp"

#define LEDS_GPIO {LED_BUILTIN}


Lights lights(LEDS_GPIO);

void setup()
{
    SerialUSB.begin(9600, SERIAL_8N1);
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
