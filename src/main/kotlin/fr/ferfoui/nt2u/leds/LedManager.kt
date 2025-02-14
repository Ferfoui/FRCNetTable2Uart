package fr.ferfoui.nt2u.leds

import fr.ferfoui.nt2u.serial.SerialCommunication
import fr.ferfoui.nt2u.serial.resetLedsCommand
import fr.ferfoui.nt2u.serial.setLedStateCommand

class LedManager(private val serial: SerialCommunication, ledCount: Int) {
    init {
        serial.open()
        serial.write(resetLedsCommand())
    }

    private val leds = (0..ledCount).associateWith { false }.toMutableMap()

    fun setLedState(ledId: Int, state: Boolean) {
        serial.write(setLedStateCommand(ledId, state))
        leds[ledId] = state
    }

    fun stop() {
        serial.write(resetLedsCommand())
        serial.close()
    }
}