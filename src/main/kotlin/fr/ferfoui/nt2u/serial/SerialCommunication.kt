package fr.ferfoui.nt2u.serial

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.jvm.Throws

class SerialCommunication(private val port: SerialPort, private val baudRate: Int = 9600) : AutoCloseable {

    @Throws(IOException::class)
    fun open() {
        if (!port.openPort())
            throw IOException("Failed to open port")

        port.setBaudRate(baudRate)
        //TODO: Check if it is the correct method to make the board read all incoming data
        //port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 2000)
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