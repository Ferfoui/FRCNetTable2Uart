package fr.ferfoui.nt2u

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.serial.SerialCommunication
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {

    val ports = SerialPort.getCommPorts()

    var serialCommunication: SerialCommunication
    var rpiPort: SerialPort? = null

    ports.forEach { port ->
        println("${port.systemPortName} : ${port.descriptivePortName}")

        if (port.systemPortName == "COM3") {
            rpiPort = port
        }
    }

    if (rpiPort != null) {
        serialCommunication = SerialCommunication(rpiPort, 115_200)
        serialCommunication.open()

        repeat(8) {
            println("Setting ON")
            serialCommunication.write("set 0 1")
            runBlocking {
                delay(1000)
            }
            println("Setting OFF")
            serialCommunication.write("set 0 0")
            runBlocking {
                delay(1000)
            }
        }
    }

}