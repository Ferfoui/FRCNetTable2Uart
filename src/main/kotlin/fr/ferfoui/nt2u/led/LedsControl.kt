package fr.ferfoui.nt2u.led

import fr.ferfoui.nt2u.model.LedConfig
import fr.ferfoui.nt2u.networktable.DashboardAccessor
import javafx.collections.ObservableList

class LedsControl(private val ledManager: LedManager, ledConfigs: ObservableList<LedConfig>) {

    private val dashboardAccessor = DashboardAccessor()

    init {
        ledConfigs.filter { it.networkTableTopic.isNotEmpty() }
            .forEach {
                subscribeToNetworkTable(it)
            }
    }

    private fun subscribeToNetworkTable(ledConfig: LedConfig) {

        when (ledConfig.valueType) {
            LedConfig.ValueType.STRING -> subscribeToString(ledConfig)
            LedConfig.ValueType.BOOLEAN -> subscribeToBoolean(ledConfig)
            LedConfig.ValueType.INT -> subscribeToInt(ledConfig)
            LedConfig.ValueType.DOUBLE -> subscribeToDouble(ledConfig)
        }
    }

    private fun subscribeToString(ledConfig: LedConfig) {
        dashboardAccessor.subscribeToString(ledConfig.networkTableTopic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, value.toBoolean())
        }
    }

    private fun subscribeToBoolean(ledConfig: LedConfig) {
        dashboardAccessor.subscribeToBoolean(ledConfig.networkTableTopic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, value)
        }
    }

    private fun subscribeToInt(ledConfig: LedConfig) {
        dashboardAccessor.subscribeToString(ledConfig.networkTableTopic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, value.toInt() != 0)
        }
    }

    private fun subscribeToDouble(ledConfig: LedConfig) {
        dashboardAccessor.subscribeToString(ledConfig.networkTableTopic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, value.toDouble() != 0.0)
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