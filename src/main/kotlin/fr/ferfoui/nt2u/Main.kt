package fr.ferfoui.nt2u

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.led.LedManager
import fr.ferfoui.nt2u.networktable.TableSubscriber
import fr.ferfoui.nt2u.networktable.initializeNetworkTableInstance
import fr.ferfoui.nt2u.serial.SerialCommunication
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    //dashboardTest()
    ledsTest()
}

fun ledsTest() {
    val ports = SerialPort.getCommPorts()

    val serialCommunication = SerialCommunication()
    var rpiPort: SerialPort? = null

    ports.forEach { port ->
        println("${port.systemPortName} : ${port.descriptivePortName}")

        if (port.systemPortName == "COM4") {
            rpiPort = port
        }
    }

    if (rpiPort != null) {
        serialCommunication.open(rpiPort!!, 115_200)

        val ledManager = LedManager(serialCommunication, 11)

        //println("Setting ON")
        //ledManager.setLedState(4, true)
        runBlocking {
            ledManager.testAllLeds()
            ledManager.simultaneousTest()
        }
        ledManager.close()
    }
}

fun dashboardTest() {
    val smartDashboard = TableSubscriber(initializeNetworkTableInstance(), SMARTDASHBOARD_NAME)

    smartDashboard.subscribeTest()

    runBlocking {
        delay(3000)
    }

}
