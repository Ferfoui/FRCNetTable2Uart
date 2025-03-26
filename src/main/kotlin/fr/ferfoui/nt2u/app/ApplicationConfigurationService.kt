package fr.ferfoui.nt2u.app

import fr.ferfoui.nt2u.led.LedConfig
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
    fun loadConfiguration(file: File = configFile): Map<String, String> {
        val config = mutableMapOf<String, String>()

        if (file.exists()) {
            val properties = Properties()
            file.inputStream().use { properties.load(it) }

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
            config["led.${ledConfig.ledNumber}.condition"] = ledConfig.condition!!.name
            config["led.${ledConfig.ledNumber}.compareValue"] = ledConfig.compareValue
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

            // We'll pass null for condition and let the LedConfig constructor set the default
            val conditionStr = config["led.${ledNumber}.condition"]
            val condition = if (conditionStr != null) {
                try {
                    LedConfig.Condition.valueOf(conditionStr)
                } catch (e: IllegalArgumentException) {
                    null // Will use default based on type
                }
            } else {
                null // Will use default based on type
            }

            val compareValue = config["led.${ledNumber}.compareValue"] ?: ""

            ledConfigs.add(LedConfig(ledNumber, topic, type, condition, compareValue))
        }

        return ledConfigs
    }
}

