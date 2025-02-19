package fr.ferfoui.nt2u

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.led.LedManager
import fr.ferfoui.nt2u.serial.SerialCommunication
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {

    val ports = SerialPort.getCommPorts()

    val serialCommunication: SerialCommunication
    var rpiPort: SerialPort? = null

    ports.forEach { port ->
        println("${port.systemPortName} : ${port.descriptivePortName}")

        if (port.systemPortName == "COM3") {
            rpiPort = port
        }
    }

    if (rpiPort != null) {
        serialCommunication = SerialCommunication(rpiPort!!, 115_200)

        val ledManager = LedManager(serialCommunication, 11)

        runBlocking {
            delay(10000)
        }
        println("Setting ON")
        ledManager.setLedState(1, true)

        /*repeat(8) {
            println("Setting ON")
            ledManager.setLedState(1, true)
            runBlocking {
                delay(1000)
            }
            println("Setting OFF")
            ledManager.setLedState(1, false)
            runBlocking {
                delay(1000)
            }
        }*/
    }

    //NetworkTableInstance.getDefault()

}