//
// Created by Ferfoui on 24/01/2025.
//

#ifndef PICO_CONTROL_LIGHTS_HPP
#define PICO_CONTROL_LIGHTS_HPP

#include <vector>

class Lights
{
public:
    explicit Lights(std::vector<int> ledsGpio);

    void setLedState(int ledIndex, bool state);
    void toggleLedState(int ledIndex);
    std::vector<bool> getLedsState() const;
    bool getLedState(int ledIndex);

private:
    std::vector<int> mLedsGpio;
    std::vector<bool> mLedsState;
};


#endif //PICO_CONTROL_LIGHTS_HPP
