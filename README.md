# FRCNetTable2Uart

This is a simple [Kotlin](https://kotlinlang.org/) program that reads values from a NetworkTable
and controls an [UART](https://en.wikipedia.org/wiki/Universal_asynchronous_receiver-transmitter) device.
It is designed to be used with
[wpilib NetworkTables](https://docs.wpilib.org/en/stable/docs/software/networktables/networktables-intro.html),
specifically the [SmartDashboard](https://docs.wpilib.org/en/stable/docs/software/dashboards/smartdashboard/index.html).

It uses values to turn on and off leds on a
[Raspberry Pi Pico](https://www.raspberrypi.org/products/raspberry-pi-pico/) (via UART).

This project aims to control leds of the [Geekos FRC team](https://frc-events.firstinspires.org/team/9220)'s
[Operator Console](https://www.chiefdelphi.com/t/operator-console-pictures/) to monitor the state of the robot.

## Installation

You can download the .jar file from the [releases](https://github.com/Ferfoui/FRCNetTable2Uart/releases) page.

You also need to have wpilib's jdk installed on your computer.
You can download wpilib from [here](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html).
It will be installed in `C:\Users\Public\wpilib\2025\jdk` by default on Windows.

## Usage

After installing the .jar file, you can run it with the following command:

```bash
C:\Users\Public\wpilib\2025\jdk\bin\java.exe -jar FRCNetTable2Uart-0.1-all.jar
```

You also have to be connected to the robot's network to be able to read the NetworkTable
and to be connected to the Raspberry Pi Pico via USB.

## NetworkTable2Uart App

The program is divided into two parts: 
- The first part is running on the computer and reads the values from the NetworkTable.
- The second part is running on the Raspberry Pi Pico and controls the leds.

### Computer Code

The code is written in Kotlin, uses wpilib library to read the values from the NetworkTable and uses
[jSerialComm](https://fazecast.github.io/jSerialComm/) library to send the values to the Raspberry Pi Pico via UART.

### Raspberry Pi Pico Code

[PlatformIO](https://platformio.org/) has been used in this project to compile the code for the Raspberry Pi Pico
with [Arduino's framework](https://docs.platformio.org/en/latest/frameworks/arduino.html).
The code is available in the [pico-control](https://github.com/Ferfoui/FRCNetTable2Uart/tree/main/pico-control) folder.

