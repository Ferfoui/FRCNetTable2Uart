//
// Created by Ferfoui on 24/01/2025.
//

#include <Arduino.h>
#include "Lights.hpp"

Lights::Lights(std::vector<int> ledsGpio) {
    mLedsGpio = ledsGpio;
    mLedsState.resize(ledsGpio.size());
    for (uint i = 0; i < ledsGpio.size(); i++) {
        pinMode(ledsGpio[i], OUTPUT);
        digitalWrite(ledsGpio[i], LOW);
    }
}

void Lights::setLedState(int ledIndex, bool state) {
    digitalWrite(mLedsGpio[ledIndex], state);
    mLedsState[ledIndex] = state;
}

void Lights::toggleLedState(int ledIndex) {
    setLedState(ledIndex, !mLedsState[ledIndex]);
}

std::vector<bool> Lights::getLedsState() const {
    return mLedsState;
}

bool Lights::getLedState(int ledIndex) {
    return mLedsState[ledIndex];
}

void Lights::setAllLedsState(bool state) {
    for (uint i = 0; i < mLedsGpio.size(); i++) {
        setLedState(i, state);
    }
}
