package fr.ferfoui.nt2u.serial

import com.fazecast.jSerialComm.SerialPort
import java.io.IOException
import kotlin.jvm.Throws

class SerialCommunication(val port: SerialPort, val baudRate: Int = 9600) : AutoCloseable {

    @Throws(IOException::class)
    fun open() {
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
}