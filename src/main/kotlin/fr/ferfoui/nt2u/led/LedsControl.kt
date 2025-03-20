package fr.ferfoui.nt2u.led

import fr.ferfoui.nt2u.model.LedConfig
import fr.ferfoui.nt2u.networktable.DashboardAccessor
import javafx.collections.ObservableList

class LedsControl(private val ledManager: LedManager, ledConfigs: ObservableList<LedConfig>) {

    private val dashboardAccessor = DashboardAccessor()

    init {
        ledConfigs.filter { it.networkTableTopic.isNotEmpty() }
            .forEach {
                dashboardAccessor.subscribe(it.networkTableTopic) { value ->
                    ledManager.setLedState(it.ledNumber, true)
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
        dashboardAccessor.close()
        ledManager.stop()
    }
    
    
}