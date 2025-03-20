package fr.ferfoui.nt2u.led

import fr.ferfoui.nt2u.model.LedConfig
import fr.ferfoui.nt2u.networktable.DashboardAccessor
import javafx.collections.ObservableList

class LedsControl(val ledManager: LedManager, ledConfigs: ObservableList<LedConfig>) {

    private val dashboardAccessor = DashboardAccessor()

    init {
        ledConfigs.forEach {
            dashboardAccessor.subscribe(it.networkTableTopic) { value ->
                ledManager.setLedState(it.ledNumber, value.toBoolean())
            }
        }
    }

    fun testAllLeds() {
        testAllLeds(ledManager)
    }

    fun simultaneousTest() {
        simultaneousTest(ledManager)
    }

    fun stop() {
        ledManager.stop()
    }
    
    
}