package fr.ferfoui.nt2u.app

import fr.ferfoui.nt2u.model.LedConfig
import java.io.File
import java.util.Properties

/**
 * Service for managing application configuration.
 */
class ApplicationConfigurationService {
    private val configFile = File(System.getProperty("user.home"), ".nt2u/config.properties")

    /**
     * Load the current configuration.
     */
    fun loadConfiguration(): Map<String, String> {
        val config = mutableMapOf<String, String>()

        if (configFile.exists()) {
            val properties = Properties()
            configFile.inputStream().use { properties.load(it) }

            for (key in properties.stringPropertyNames()) {
                config[key] = properties.getProperty(key)
            }
        }

        return config
    }

    /**
     * Save the configuration.
     */
    fun saveConfiguration(config: Map<String, String>) {
        // Ensure directory exists
        configFile.parentFile.mkdirs()

        val properties = Properties()
        for ((key, value) in config) {
            properties.setProperty(key, value)
        }

        configFile.outputStream().use { properties.store(it, "NetTables2UART Configuration") }
    }

    /**
     * Save LED configurations.
     */
    fun saveLedConfigurations(ledConfigs: List<LedConfig>) {
        val config = loadConfiguration().toMutableMap()

        // Clear any existing LED configurations
        val keysToRemove = config.keys.filter { it.startsWith("led.") }
        keysToRemove.forEach { config.remove(it) }

        // Save new LED configurations
        for (ledConfig in ledConfigs) {
            config["led.${ledConfig.ledNumber}.topic"] = ledConfig.networkTableTopic
            config["led.${ledConfig.ledNumber}.type"] = ledConfig.valueType.name
        }

        saveConfiguration(config)
    }

    /**
     * Load LED configurations.
     */
    fun loadLedConfigurations(): List<LedConfig> {
        val config = loadConfiguration()
        val ledConfigs = mutableListOf<LedConfig>()

        // Default configuration for LEDs 3-10 if not found
        for (ledNumber in 3..10) {
            val topic = config["led.${ledNumber}.topic"] ?: ""
            val typeStr = config["led.${ledNumber}.type"] ?: LedConfig.ValueType.BOOLEAN.name
            val type = try {
                LedConfig.ValueType.valueOf(typeStr)
            } catch (e: IllegalArgumentException) {
                LedConfig.ValueType.BOOLEAN
            }

            ledConfigs.add(LedConfig(ledNumber, topic, type))
        }

        return ledConfigs
    }
}

