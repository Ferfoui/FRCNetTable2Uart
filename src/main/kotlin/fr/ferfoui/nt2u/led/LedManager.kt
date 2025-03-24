package fr.ferfoui.nt2u.led

import fr.ferfoui.nt2u.serial.SerialCommunication
import fr.ferfoui.nt2u.serial.resetLedsCommand
import fr.ferfoui.nt2u.serial.setLedStateCommand
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException

class LedManager(private val serial: SerialCommunication, val ledCount: Int) {
    init {
        serial.write(resetLedsCommand())
    }

    private val leds = (0..ledCount).associateWith { false }.toMutableMap()

    fun setLedState(ledId: Int, state: Boolean) {
        serial.write(setLedStateCommand(ledId, state))
        leds[ledId] = state
    }

    fun stop() {
        try {
            serial.write(resetLedsCommand())
        } catch (_: IOException) {
        } finally {
            serial.close()
        }
    }
}

fun testAllLeds(ledManager: LedManager) {
    repeat(ledManager.ledCount) {
        println("Setting $it ON")
        ledManager.setLedState(it, true)
        runBlocking {
            delay(500)
        }
        println("Setting $it OFF")
        ledManager.setLedState(it, false)
        runBlocking {
            delay(500)
        }
    }
}

fun simultaneousTest(ledManager: LedManager) {
    repeat(ledManager.ledCount) {
        println("Setting $it ON")
        ledManager.setLedState(it, true)
    }
    runBlocking {
        delay(1000)
    }
    repeat(ledManager.ledCount) {
        println("Setting $it OFF")
        ledManager.setLedState(it, false)
    }
}