package fr.ferfoui.nt2u.led

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

/**
 * Model class representing the configuration for a single LED.
 */
class LedConfig(
    ledNumber: Int,
    networkTableTopic: String = "",
    valueType: ValueType = ValueType.BOOLEAN,
    condition: Condition = Condition.EQUALS_TRUE,
    compareValue: String = ""
) {
    val ledNumberProperty = SimpleIntegerProperty(ledNumber)
    val networkTableTopicProperty = SimpleStringProperty(networkTableTopic)
    val valueTypeProperty = SimpleObjectProperty(valueType)
    val conditionProperty = SimpleObjectProperty(condition)
    val compareValueProperty = SimpleStringProperty(compareValue)

    var ledNumber: Int
        get() = ledNumberProperty.get()
        set(value) = ledNumberProperty.set(value)

    var networkTableTopic: String
        get() = networkTableTopicProperty.get()
        set(value) = networkTableTopicProperty.set(value)

    var valueType: ValueType
        get() = valueTypeProperty.get()
        set(value) {
            valueTypeProperty.set(value)
            // Update condition based on new value type
            condition = getDefaultConditionForType(value)
        }

    var condition: Condition
        get() = conditionProperty.get()
        set(value) = conditionProperty.set(value)

    var compareValue: String
        get() = compareValueProperty.get()
        set(value) = compareValueProperty.set(value)
    
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

    enum class Condition(val displayName: String, val applicableTypes: List<ValueType>) {
        EQUALS_TRUE("value == true", listOf(ValueType.BOOLEAN)),
        NOT_EQUALS_TRUE("value != true", listOf(ValueType.BOOLEAN)),

        GREATER_THAN("value > [value]", listOf(ValueType.DOUBLE, ValueType.INT)),
        LESS_THAN("value < [value]", listOf(ValueType.DOUBLE, ValueType.INT)),

        EQUALS("value == [value]", listOf(ValueType.STRING, ValueType.INT, ValueType.DOUBLE)),
        NOT_EQUALS("value != [value]", listOf(ValueType.STRING, ValueType.INT, ValueType.DOUBLE));

        override fun toString(): String {
            return displayName
        }
    }

    private fun getDefaultConditionForType(type: ValueType): Condition {
        return when (type) {
            ValueType.BOOLEAN -> Condition.EQUALS_TRUE
            ValueType.DOUBLE -> Condition.GREATER_THAN
            ValueType.INT -> Condition.EQUALS
            ValueType.STRING -> Condition.EQUALS
        }
    }

    /**
     * Get available conditions for the current value type
     */
    fun getAvailableConditions(): List<Condition> {
        return Condition.entries.filter { it.applicableTypes.contains(valueType) }
    }
}

