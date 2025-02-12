//
// Created by Ferfoui on 12/02/2025.
//

#include "Command.hpp"
#include <sstream>

Command::Command(CommandType type, std::vector<String> args) : mType(type), mArgs(args) {}

CommandType Command::getType() const {
    return mType;
}

std::vector<String> Command::getArgs() const {
    return mArgs;
}


std::vector<String> split(const String& str) {
    std::vector<String> result;
    int start = 0;
    int end = str.indexOf(' ');

    while (end != -1) {
        result.push_back(str.substring(start, end));
        start = end + 1;
        end = str.indexOf(' ', start);
    }

    result.push_back(str.substring(start));
    return result;
}

bool checkArgsNumber(const int argsNumber, CommandType type) {
  	switch (type) {
    case set:
        return argsNumber == SET_COMMAND_ARGS;
    case get:
        return argsNumber == GET_COMMAND_ARGS;
    case unknown:
       	return false;
    }
}

Command parseCommand(const String& input) {
    CommandType type = unknown;
    if (input.startsWith(SET_COMMAND)) {
        type = set;
    } else if (input.startsWith(GET_COMMAND)) {
        type = get;
    } else {
        return Command(unknown, {});
    }

    std::vector<String> args = split(input);
    args.erase(args.begin());

    if (!checkArgsNumber(args.size(), type)) {
        return Command(unknown, {});
    }
    return Command(type, args);
}
