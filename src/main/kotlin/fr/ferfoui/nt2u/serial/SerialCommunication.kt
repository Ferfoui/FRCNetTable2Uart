package fr.ferfoui.nt2u.serial

import com.fazecast.jSerialComm.SerialPort
import java.io.IOException
import kotlin.jvm.Throws

class SerialCommunication : AutoCloseable {

    private lateinit var port: SerialPort

    @Throws(IOException::class)
    fun open(serialPort: SerialPort, baudRate: Int) {
        this.port = serialPort
        if (!port.openPort())
            throw IOException("Failed to open port")

        port.setBaudRate(baudRate)
    }

    override fun close() {
        port.closePort()
    }

    fun write(data: String) {
        port.outputStream.write(data.toByteArray())
    }

    fun dataAvailable(): Boolean {
        return port.bytesAvailable() > 0
    }

    fun read(): String {
        val buffer = ByteArray(1024)
        val bytesRead = port.inputStream.read(buffer)
        return String(buffer, 0, bytesRead)
    }

    fun isOpen() = port.isOpen
}

fun getAvailableComPorts() =
    SerialPort.getCommPorts().map { it.systemPortName }

fun getAvailableBaudRates() =
    listOf(9600, 19200, 38400, 57600, 115200, 230400, 460800, 921600)
