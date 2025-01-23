#include <Arduino.h>
void setup() {
    SerialUSB.begin(9600, SERIAL_8N1);

    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, LOW);
}

void loop() {
    if (SerialUSB.available()) {
        const String input = SerialUSB.readString();
        SerialUSB.println(input);

        if (input.equals("H")) {
            digitalWrite(LED_BUILTIN, HIGH);
        } else if (input.equals("L")) {
            digitalWrite(LED_BUILTIN, LOW);
        }
    }
}