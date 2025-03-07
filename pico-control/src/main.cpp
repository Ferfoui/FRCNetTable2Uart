//
// Created by Ferfoui on 23/01/2025.
//

#include <Arduino.h>
#include "Lights.hpp"
#include "Command.hpp"

#define BAUD_RATE 115200
#define LEDS_GPIO {LED_BUILTIN, 28, 27, 26, 22, 21, 20, 19, 18, 17, 16}

Lights lights(LEDS_GPIO);

void setup()
{
    SerialUSB.begin(BAUD_RATE, SERIAL_8N1);
    lights.setLedState(1, true);
}

void commandLogic(const Command& command)
{
    const std::vector<String> args = command.getArgs();

    switch (command.getType())
    {
    case set:
        lights.setLedState(args.at(0).toInt(), args.at(1).toInt());
        break;
    case get:
        SerialUSB.println(String(lights.getLedState(args.at(0).toInt())));
        break;
    case reset:
        lights.setAllLedsState(false);
        lights.setLedState(1, true);
    case unknown:
        break;
    }
}

void loop()
{
    if (SerialUSB.available())
    {
        const String input = SerialUSB.readStringUntil('\n');
        const Command command = parseCommand(input);

        commandLogic(command);
    }
}
