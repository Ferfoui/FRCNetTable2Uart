package fr.ferfoui.nt2u.led

import fr.ferfoui.nt2u.NET_TABLE_TOPIC_SEPARATOR
import fr.ferfoui.nt2u.SMARTDASHBOARD_NAME
import fr.ferfoui.nt2u.THIRD_NETWORK_TABLE_INDICATOR
import fr.ferfoui.nt2u.networktable.TableSubscriber
import fr.ferfoui.nt2u.networktable.initializeNetworkTableInstance

/**
 * LedsControl is responsible for managing LED states based on network table subscriptions.
 *
 * @param ledManager The LedManager instance to control the LEDs.
 * @param ledConfigs A list of LED configurations to subscribe to.
 */
class LedsControl(private val ledManager: LedManager, ledConfigs: List<LedConfig>) {

    private val instance = initializeNetworkTableInstance()
    private val dashboardSubscriber: TableSubscriber = TableSubscriber(instance, SMARTDASHBOARD_NAME)
    private val thirdNetworkTables = mutableMapOf<String, TableSubscriber>()

    init {
        ledConfigs.filter { it.networkTableTopic.isNotEmpty() }
            .forEach {
                subscribeToNetworkTable(it)
            }
    }

    /**
     * Subscribe to the network table for the given LED configuration.
     * The subscription type is determined by the valueType of the LED configuration.
     *
     * @param ledConfig The LED configuration to subscribe to.
     */
    private fun subscribeToNetworkTable(ledConfig: LedConfig) {
        when (ledConfig.valueType) {
            LedConfig.ValueType.STRING -> subscribeToString(ledConfig)
            LedConfig.ValueType.BOOLEAN -> subscribeToBoolean(ledConfig)
            LedConfig.ValueType.INT -> subscribeToInt(ledConfig)
            LedConfig.ValueType.DOUBLE -> subscribeToDouble(ledConfig)
        }
    }

    /**
     * Subscribe to the network table for a string value.
     *
     * @param ledConfig The LED configuration to subscribe to.
     */
    private fun subscribeToString(ledConfig: LedConfig) {
        val (networkTable, topic) = identifyNetworkTableAndTopic(ledConfig)
        networkTable.subscribeToString(topic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, ledConfig.compare(value))
        }
    }

    /**
     * Subscribe to the network table for a boolean value.
     *
     * @param ledConfig The LED configuration to subscribe to.
     */
    private fun subscribeToBoolean(ledConfig: LedConfig) {
        val (networkTable, topic) = identifyNetworkTableAndTopic(ledConfig)
        networkTable.subscribeToBoolean(topic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, ledConfig.compare(value.toString()))
        }
    }

    /**
     * Subscribe to the network table for an integer value.
     *
     * @param ledConfig The LED configuration to subscribe to.
     */
    private fun subscribeToInt(ledConfig: LedConfig) {
        val (networkTable, topic) = identifyNetworkTableAndTopic(ledConfig)
        networkTable.subscribeToInteger(topic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, ledConfig.compare(value.toString()))
        }
    }

    /**
     * Subscribe to the network table for a double value.
     *
     * @param ledConfig The LED configuration to subscribe to.
     */
    private fun subscribeToDouble(ledConfig: LedConfig) {
        val (networkTable, topic) = identifyNetworkTableAndTopic(ledConfig)
        networkTable.subscribeToDouble(topic) { value ->
            ledManager.setLedState(ledConfig.ledNumber, ledConfig.compare(value.toString()))
        }
    }

    /**
     * Identify the network table and topic for the given LED configuration.
     *
     * @param ledConfig The LED configuration to identify.
     * @return A pair containing the network table and topic.
     */
    private fun identifyNetworkTableAndTopic(ledConfig: LedConfig): Pair<TableSubscriber, String> {
        val networkTable = identifyNetworkTable(ledConfig)
        val topic = identifyTopic(ledConfig)
        return Pair(networkTable, topic)
    }

    /**
     * Identify the network table for the given LED configuration.
     *
     * @param ledConfig The LED configuration to identify.
     * @return The network table accessor.
     */
    private fun identifyNetworkTable(ledConfig: LedConfig): TableSubscriber {
        if (ledConfig.networkTableTopic.startsWith(THIRD_NETWORK_TABLE_INDICATOR)) {
            val tableName = ledConfig.networkTableTopic.split(NET_TABLE_TOPIC_SEPARATOR)[1]
            return getNetworkTable(tableName)
        }
        return dashboardSubscriber
    }

    /**
     * Identify the topic for the given LED configuration.
     *
     * @param ledConfig The LED configuration to identify.
     * @return The topic string.
     */
    private fun identifyTopic(ledConfig: LedConfig): String {
        return if (ledConfig.networkTableTopic.startsWith(THIRD_NETWORK_TABLE_INDICATOR)) {

            // Remove the first parts of the topic ("INDICATOR" and table name)
            ledConfig.networkTableTopic
                .split(NET_TABLE_TOPIC_SEPARATOR)
                .drop(2)
                .joinToString(NET_TABLE_TOPIC_SEPARATOR.toString())

        } else {
            ledConfig.networkTableTopic
        }
    }

    /**
     * Get or create a network table accessor for the given table name.
     *
     * @param tableName The name of the network table.
     * @return The network table accessor.
     */
    private fun getNetworkTable(tableName: String): TableSubscriber {
        return thirdNetworkTables.getOrPut(tableName) {
            TableSubscriber(initializeNetworkTableInstance(), tableName)
        }
    }

    fun testAllLeds() {
        testAllLeds(ledManager)
    }

    fun simultaneousTest() {
        simultaneousTest(ledManager)
    }

    /**
     * Stop the LED control and close all network tables.
     */
    fun stop() {
        dashboardSubscriber.close()
        thirdNetworkTables.values.forEach { it.close() }
        ledManager.stop()
    }


}