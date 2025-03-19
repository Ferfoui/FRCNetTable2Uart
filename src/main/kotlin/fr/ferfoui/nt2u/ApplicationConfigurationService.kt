package fr.ferfoui.nt2u

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
}

