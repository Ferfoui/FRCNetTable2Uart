//
// Created by Ferfoui on 12/02/2025.
//

#ifndef COMMAND_HPP
#define COMMAND_HPP

#include <Arduino.h>
#include <vector>

enum CommandType { set, get, unknown };

class Command {
public:
    explicit Command(CommandType type, std::vector<String> args);
    CommandType getType() const;
    std::vector<String> getArgs() const;

private:
    const CommandType mType;
    const std::vector<String> mArgs;
};

Command parseCommand(const String& input);

#endif //COMMAND_HPP
