package fr.ferfoui.nt2u.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

/**
 * Model class representing the configuration for a single LED.
 */
class LedConfig(
    ledNumber: Int,
    networkTableTopic: String = "",
    valueType: ValueType = ValueType.BOOLEAN
) {
    val ledNumberProperty = SimpleIntegerProperty(ledNumber)
    val networkTableTopicProperty = SimpleStringProperty(networkTableTopic)
    val valueTypeProperty = SimpleObjectProperty(valueType)
    
    var ledNumber: Int
        get() = ledNumberProperty.get()
        set(value) = ledNumberProperty.set(value)
    
    var networkTableTopic: String
        get() = networkTableTopicProperty.get()
        set(value) = networkTableTopicProperty.set(value)
    
    var valueType: ValueType
        get() = valueTypeProperty.get()
        set(value) = valueTypeProperty.set(value)
    
    enum class ValueType {
        STRING, BOOLEAN, INT, DOUBLE;

        override fun toString(): String {
            return name.lowercase().capitalize()
        }

        private fun String.capitalize(): String {
            return if (this.isEmpty()) this
            else this[0].uppercase() + this.substring(1)
        }
    }
}

