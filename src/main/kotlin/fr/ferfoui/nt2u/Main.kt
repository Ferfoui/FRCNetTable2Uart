package fr.ferfoui.nt2u

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.led.LedManager
import fr.ferfoui.nt2u.led.testAllLeds
import fr.ferfoui.nt2u.serial.SerialCommunication
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {

    val ports = SerialPort.getCommPorts()

    val serialCommunication: SerialCommunication
    var rpiPort: SerialPort? = null

    ports.forEach { port ->
        println("${port.systemPortName} : ${port.descriptivePortName}")

        if (port.systemPortName == "COM4") {
            rpiPort = port
        }
    }

    if (rpiPort != null) {
        serialCommunication = SerialCommunication(rpiPort!!, 115_200)

        val ledManager = LedManager(serialCommunication, 11)

        runBlocking {
            delay(7000)
        }
        //println("Setting ON")
        //ledManager.setLedState(4, true)

        testAllLeds(ledManager)

        ledManager.stop()
    }

    //NetworkTableInstance.getDefault()

}
