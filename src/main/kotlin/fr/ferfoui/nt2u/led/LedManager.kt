package fr.ferfoui.nt2u.led

import fr.ferfoui.nt2u.serial.SerialCommunication
import fr.ferfoui.nt2u.serial.resetLedsCommand
import fr.ferfoui.nt2u.serial.setLedStateCommand
import kotlinx.coroutines.delay
import java.io.IOException

class LedManager(private val serial: SerialCommunication, val ledCount: Int) : AutoCloseable {
    init {
        serial.write(resetLedsCommand())
    }

    private val leds = (0..ledCount).associateWith { false }.toMutableMap()

    @Throws(exceptionClasses = [IOException::class])
    fun setLedState(ledId: Int, state: Boolean) {
        serial.write(setLedStateCommand(ledId, state))
        leds[ledId] = state
    }

    override fun close() {
        try {
            serial.write(resetLedsCommand())
        } catch (_: IOException) {
        } finally {
            serial.close()
        }
    }

    suspend fun testAllLeds() {
        repeat(ledCount) {
            println("Setting $it ON")
            setLedState(it, true)
            delay(500)

            println("Setting $it OFF")
            setLedState(it, false)
            delay(500)
        }
    }

    suspend fun simultaneousTest() {
        repeat(ledCount) {
            println("Setting $it ON")
            setLedState(it, true)
        }

        delay(1000)

        repeat(ledCount) {
            println("Setting $it OFF")
            setLedState(it, false)
        }
    }
}

