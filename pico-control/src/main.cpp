//
// Created by Ferfoui on 23/01/2025.
//

#include <Arduino.h>
#include "SerialCommunication.hpp"
#include "Lights.hpp"
#include "Command.hpp"

#define LEDS_GPIO {LED_BUILTIN, 28, 27, 26, 22, 21, 20, 19, 18, 17, 16}

SerialCommunication serial;
Lights lights(LEDS_GPIO);

void setup()
{
    serial.init(SerialUSB);
}

void loop()
{
    if (serial.available())
    {
        const Command command = serial.readCommand();

        if (command.getType() == unknown)
        {
            return;
        }

        const std::vector<String> args = command.getArgs();

        if (command.getType() == set)
        {
            if (args.size() != 2)
                return;

            const int led = args[0].toInt();
            const int state = args[1].toInt();

            lights.setLedState(led, state);
        }
        else if (command.getType() == get)
        {
            if (args.size() != 1)
            {
                return;
            }

            const int led = args[0].toInt();
            const int state = lights.getLedsState().at(led);

            SerialUSB.println(state);
        }
    }
}
