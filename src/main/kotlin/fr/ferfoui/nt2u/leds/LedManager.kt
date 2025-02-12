package fr.ferfoui.nt2u.leds

import fr.ferfoui.nt2u.serial.SerialCommunication
import fr.ferfoui.nt2u.serial.resetLedsCommand
import fr.ferfoui.nt2u.serial.setLedStateCommand

class LedManager(val serialCommunication: SerialCommunication, ledCount: Int) {

    val leds = (1..ledCount).associate { it to false }

    init {
        serialCommunication.open()
        serialCommunication.write(resetLedsCommand())
    }

    fun setLedState(ledId: Int, state: Int) {
        serialCommunication.write(setLedStateCommand(ledId, state))
    }

    fun stop() {
        serialCommunication.write(resetLedsCommand())
        serialCommunication.close()
    }
}