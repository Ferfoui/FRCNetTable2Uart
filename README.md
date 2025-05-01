# FRCNetTable2Uart

This is a [Kotlin](https://kotlinlang.org/) app that reads values from a NetworkTable
and controls a [UART](https://en.wikipedia.org/wiki/Universal_asynchronous_receiver-transmitter) device.
It is designed to be used with
[WPILib NetworkTables](https://docs.wpilib.org/en/stable/docs/software/networktables/networktables-intro.html),
specifically the [SmartDashboard](https://docs.wpilib.org/en/stable/docs/software/dashboards/smartdashboard/index.html).

It uses values to turn LEDs on and off on a
[Raspberry Pi Pico](https://www.raspberrypi.org/products/raspberry-pi-pico/) (via UART).

This project aims to control the LEDs of the [Geekos FRC team](https://frc-events.firstinspires.org/team/9220)'s
[Operator Console](https://www.chiefdelphi.com/t/operator-console-pictures/) to monitor the state of the robot.

## Installation

You can download `FRCNetTable2Uart-[version].zip` package from the [release page](https://github.com/Ferfoui/FRCNetTable2Uart/releases).
You only need to unzip it, and that's it!
(You can also download a .jar file from the release page, but you will need to use the command line to run it.)

You also need to have WPILib's JDK installed on your computer.
You can download WPILib from [here](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html).
It will be installed in `C:\Users\Public\wpilib\2025\jdk` by default on Windows.

## Running the Program

You must be connected to the robot's network to read the NetworkTable
and connected to the Raspberry Pi Pico via USB.
(It is not mandatory, but it is better if you want to see the LEDs turning on and off.)

### From Zip Package

After unzipping the package, you can run the program by double-clicking the `run.bat` file.

### From .jar File

After downloading the .jar file, you can run it with the following command:

```bash
C:\Users\Public\wpilib\2025\jdk\bin\java.exe -jar FRCNetTable2Uart-0.2-all.jar
```

## NetworkTable2Uart App

The program is divided into two parts: 
- The first part runs on the computer and reads the values from the NetworkTable.
- The second part runs on the Raspberry Pi Pico and controls the LEDs.

### Computer Code

The code is written in Kotlin, uses the WPILib library to read the values from the NetworkTable, and uses the
[jSerialComm](https://fazecast.github.io/jSerialComm/) library to send the values to the Raspberry Pi Pico via UART.

### Raspberry Pi Pico Code

[PlatformIO](https://platformio.org/) has been used in this project to compile the code for the Raspberry Pi Pico
with the [Arduino framework](https://docs.platformio.org/en/latest/frameworks/arduino.html).
The code is available in the [pico-control](pico-control) folder.
