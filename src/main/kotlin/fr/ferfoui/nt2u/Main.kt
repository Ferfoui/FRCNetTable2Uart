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
        println(port.systemPortName)

        if (port.systemPortName == "COM3") {
            rpiPort = port
        }
    }

    if (rpiPort != null) {
        serialCommunication = SerialCommunication(rpiPort)
        serialCommunication.open()

        repeat(8) {
            println("Setting ON")
            serialCommunication.write("H")
            runBlocking {
                delay(1000)
            }
            println("Setting OFF")
            serialCommunication.write("L")
            runBlocking {
                delay(1000)
            }
        }
    }

}