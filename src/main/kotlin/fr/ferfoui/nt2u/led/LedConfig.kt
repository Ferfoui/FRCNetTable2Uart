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
    condition: Condition? = null,
    compareValue: String = ""
) {
    val ledNumberProperty = SimpleIntegerProperty(ledNumber)
    val networkTableTopicProperty = SimpleStringProperty(networkTableTopic)
    val valueTypeProperty = SimpleObjectProperty(valueType)
    val conditionProperty = SimpleObjectProperty<Condition>()
    val compareValueProperty = SimpleStringProperty(compareValue)

    init {
        // Ensure we have a valid condition for the value type
        conditionProperty.set(condition ?: getDefaultConditionForType(valueType))
    }

    var ledNumber: Int
        get() = ledNumberProperty.get()
        set(value) = ledNumberProperty.set(value)

    var networkTableTopic: String
        get() = networkTableTopicProperty.get()
        set(value) = networkTableTopicProperty.set(value)

    var valueType: ValueType
        get() = valueTypeProperty.get()
        set(value) {
            val oldType = valueTypeProperty.get()
            valueTypeProperty.set(value)

            // Update condition based on new value type if the current condition
            // is not applicable to the new type
            val currentCondition = conditionProperty.get()
            if (!currentCondition.applicableTypes.contains(value)) {
                condition = getDefaultConditionForType(value)
            }

            // Reset compare value when type changes
            if (oldType != value) {
                resetCompareValue()
            }
        }

    var condition: Condition?
        get() = conditionProperty.get()
        set(value) {
            // Never allow null condition - use default if null is provided
            conditionProperty.set(value ?: getDefaultConditionForType(valueType))
        }

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

    enum class Condition(val displayName: String, val applicableTypes: List<ValueType>, val compare: (String, String) -> Boolean) {
        EQUALS_TRUE("value == true", listOf(ValueType.BOOLEAN), { value, _ -> value == "true" }),
        NOT_EQUALS_TRUE("value != true", listOf(ValueType.BOOLEAN), { value, _ -> value != "true" }),

        GREATER_THAN("value > [value]", listOf(ValueType.DOUBLE, ValueType.INT), { value, compareValue -> value.toDouble() > compareValue.toDouble() }),
        LESS_THAN("value < [value]", listOf(ValueType.DOUBLE, ValueType.INT), { value, compareValue -> value.toDouble() < compareValue.toDouble() }),

        EQUALS("value == [value]", listOf(ValueType.STRING, ValueType.INT, ValueType.DOUBLE), { value, compareValue -> value == compareValue }),
        NOT_EQUALS("value != [value]", listOf(ValueType.STRING, ValueType.INT, ValueType.DOUBLE), { value, compareValue -> value != compareValue });

        override fun toString(): String {
            return displayName
        }
    }

    /**
     * Get the default condition for a given value type
     */
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

    /**
     * Compare the given value with the compare value based on the current condition
     */
    fun compare(value: String): Boolean {
        return condition!!.compare(value, compareValue)
    }

    /**
     * Reset the compare value based on the current value type
     */
    fun resetCompareValue() {
        compareValue = when (valueType) {
            ValueType.BOOLEAN -> ""
            ValueType.INT -> "0"
            ValueType.DOUBLE -> "0.0"
            ValueType.STRING -> ""
        }
    }

    /**
     * Validate if the given string is valid for the current value type
     */
    fun isValidCompareValue(value: String): Boolean {
        return when (valueType) {
            ValueType.BOOLEAN -> true // Boolean doesn't use compare value
            ValueType.INT -> value.toIntOrNull() != null
            ValueType.DOUBLE -> value.toDoubleOrNull() != null
            ValueType.STRING -> true // Any string is valid for the string type
        }
    }

}

