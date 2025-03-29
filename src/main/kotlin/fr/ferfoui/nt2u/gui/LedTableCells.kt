package fr.ferfoui.nt2u.gui

import fr.ferfoui.nt2u.led.LedConfig
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TextField

class CompareValueTableCell(compareValueColumn: TableColumn<LedConfig, String>) : TableCell<LedConfig, String>() {
    private val textField = TextField()

    init {
        textField.setOnAction {
            if (isEditing) {
                commitEdit(textField.text)
            }
        }

        textField.focusedProperty().addListener { _, _, newValue ->
            if (!newValue && isEditing) {
                // Validate and commit when focus is lost
                if (validateInput()) {
                    commitEdit(textField.text)
                } else {
                    cancelEdit()
                }
            }
        }

        // Make the text field expand to fill available space
        textField.prefWidthProperty().bind(compareValueColumn.widthProperty().subtract(5))
    }

    private fun validateInput(): Boolean {
        val ledConfig = tableRow.item
        return ledConfig.isValidCompareValue(textField.text)
    }

    override fun startEdit() {
        // Don't allow editing for Boolean type
        val ledConfig = tableRow.item
        if (ledConfig.valueType == LedConfig.ValueType.BOOLEAN) {
            return
        }

        super.startEdit()
        if (!isEmpty) {
            textField.text = item ?: ""
            text = null
            graphic = textField
            textField.requestFocus()
            textField.selectAll()
        }
    }

    override fun cancelEdit() {
        super.cancelEdit()
        text = item
        graphic = null
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            text = null
            graphic = null
            style = ""
        } else {
            if (isEditing) {
                textField.text = item ?: ""
                text = null
                graphic = textField
            } else {
                text = item ?: ""
                graphic = null

                // Style based on the value type
                val ledConfig = tableRow?.item
                if (ledConfig != null) {
                    style = when (ledConfig.valueType) {
                        LedConfig.ValueType.BOOLEAN -> {
                            "-fx-text-fill: gray; -fx-font-style: italic;"
                        }

                        else -> {
                            ""
                        }
                    }
                }
            }
        }
    }

    override fun commitEdit(newValue: String?) {
        // Validate the input before committing
        val ledConfig = tableRow.item
        if (ledConfig != null && newValue != null) {
            if (ledConfig.isValidCompareValue(newValue)) {
                super.commitEdit(newValue)
            } else {
                // Show error styling
                textField.style = "-fx-border-color: red;"

                // Show error tooltip
                val tooltip = when (ledConfig.valueType) {
                    LedConfig.ValueType.INT -> "Please enter a valid integer"
                    LedConfig.ValueType.DOUBLE -> "Please enter a valid number"
                    else -> "Invalid input"
                }

                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Invalid Input"
                alert.headerText = "Invalid Compare Value"
                alert.contentText = tooltip
                alert.showAndWait()

                // Reset to previous value
                cancelEdit()
            }
        } else {
            super.commitEdit(newValue)
        }
    }
}

class ConditionTableCell : TableCell<LedConfig, LedConfig.Condition>() {
    private val comboBox = ComboBox<LedConfig.Condition>()

    init {
        comboBox.setOnAction {
            if (isEditing) {
                commitEdit(comboBox.selectionModel.selectedItem)
            }
        }
    }

    override fun startEdit() {
        super.startEdit()
        if (!isEmpty) {
            val ledConfig = tableRow.item
            val availableConditions = ledConfig.getAvailableConditions()

            comboBox.items.clear()
            comboBox.items.addAll(availableConditions)
            comboBox.selectionModel.select(item)

            text = null
            graphic = comboBox
            comboBox.show()
        }
    }

    override fun cancelEdit() {
        super.cancelEdit()
        text = item?.toString()
        graphic = null
    }

    override fun updateItem(item: LedConfig.Condition?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            text = null
            graphic = null
        } else if (item == null) {
            // If the condition is null, get the default condition for the current value type
            val ledConfig = tableRow?.item
            if (ledConfig != null) {
                // This will trigger the setter which will set a default condition
                ledConfig.condition = null
                // Update the display with the new default condition
                text = ledConfig.condition.toString()
            } else {
                text = "Unknown"
            }
            graphic = null
        } else {
            if (isEditing) {
                comboBox.selectionModel.select(item)
                text = null
                graphic = comboBox
            } else {
                text = item.toString()
                graphic = null
            }
        }
    }
}
