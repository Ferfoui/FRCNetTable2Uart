//
// Created by Ferfoui on 23/01/2025.
//

#include <Arduino.h>
#include "Lights.hpp"
#include "Command.hpp"

#define LEDS_GPIO {LED_BUILTIN, 28, 27, 26, 22, 21, 20, 19, 18, 17, 16}

Lights lights(LEDS_GPIO);

void setup()
{
    SerialUSB.begin(9600, SERIAL_8N1);
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
    case unknown:
        break;
    }
}

void loop()
{
    if (SerialUSB.available())
    {
        const String input = SerialUSB.readString();
        const Command command = parseCommand(input);

        commandLogic(command);
    }
}
