package fr.ferfoui.nt2u.gui

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.app.ApplicationConfigurationService
import fr.ferfoui.nt2u.led.LedManager
import fr.ferfoui.nt2u.led.LedsControl
import fr.ferfoui.nt2u.led.LedConfig
import fr.ferfoui.nt2u.serial.SerialCommunication
import fr.ferfoui.nt2u.serial.getAvailableBaudRates
import fr.ferfoui.nt2u.serial.getAvailableComPorts
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage

class AppConfigController {

    @FXML private lateinit var comPortComboBox: ComboBox<String>
    @FXML private lateinit var baudRateComboBox: ComboBox<Int>
    @FXML private lateinit var connectionStatusCircle: Circle
    @FXML private lateinit var connectionStatusLabel: Label
    @FXML private lateinit var connectButton: Button
    @FXML private lateinit var disconnectButton: Button
    @FXML private lateinit var saveButton: Button
    @FXML private lateinit var cancelButton: Button
    @FXML private lateinit var autoConnectCheckBox: CheckBox
    @FXML private lateinit var ledTableView: TableView<LedConfig>
    @FXML private lateinit var ledNumberColumn: TableColumn<LedConfig, Int>
    @FXML private lateinit var topicColumn: TableColumn<LedConfig, String>
    @FXML private lateinit var valueTypeColumn: TableColumn<LedConfig, LedConfig.ValueType>
    @FXML private lateinit var conditionColumn: TableColumn<LedConfig, LedConfig.Condition>
    @FXML private lateinit var compareValueColumn: TableColumn<LedConfig, String>

    private lateinit var ledsControl: LedsControl

    private val ledConfigs: ObservableList<LedConfig> = FXCollections.observableArrayList()

    private val configService = ApplicationConfigurationService()

    private val isConnected = SimpleBooleanProperty(false)
    private val statusText = SimpleStringProperty("Not Connected")

    private val serialCommunication = SerialCommunication()

    @FXML
    fun initialize() {
        // Initialize ComboBoxes
        refreshComPorts()
        baudRateComboBox.items = FXCollections.observableArrayList(
            getAvailableBaudRates()
        )
        baudRateComboBox.selectionModel.select(4) // Default to 115200

        // Initialize LED table
        initializeLedTable()

        // Bind connection status to UI
        connectionStatusLabel.textProperty().bind(statusText)

        isConnected.addListener { _, _, newValue ->
            if (newValue) {
                connectionStatusCircle.fill = Color.GREEN
                statusText.set("Connected")
                connectButton.isDisable = true
                disconnectButton.isDisable = false
            } else {
                connectionStatusCircle.fill = Color.RED
                statusText.set("Not Connected")
                connectButton.isDisable = false
                disconnectButton.isDisable = true
            }
        }

        // Initialize buttons state
        disconnectButton.isDisable = true

        // Load saved configuration
        loadConfiguration()
    }

    private fun initializeLedTable() {
        // Set up the columns
        ledNumberColumn.cellValueFactory = PropertyValueFactory("ledNumber")

        topicColumn.cellValueFactory = PropertyValueFactory("networkTableTopic")
        topicColumn.cellFactory = TextFieldTableCell.forTableColumn()
        topicColumn.setOnEditCommit { event ->
            val ledConfig = event.rowValue
            ledConfig.networkTableTopic = event.newValue
        }

        valueTypeColumn.cellValueFactory = PropertyValueFactory("valueType")
        valueTypeColumn.cellFactory = ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(*LedConfig.ValueType.entries.toTypedArray())
        )
        valueTypeColumn.setOnEditCommit { event ->
            val ledConfig = event.rowValue
            ledConfig.valueType = event.newValue

            // Refresh the table to update condition options and compare value
            ledTableView.refresh()
        }

        // Set up condition column with dynamic options based on the value type
        conditionColumn.cellValueFactory = PropertyValueFactory("condition")
        conditionColumn.setCellFactory {
            object : TableCell<LedConfig, LedConfig.Condition>() {
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
        }
        conditionColumn.setOnEditCommit { event ->
            val ledConfig = event.rowValue
            ledConfig.condition = event.newValue
        }

        // Set up compare value column with type validation
        compareValueColumn.cellValueFactory = PropertyValueFactory("compareValue")
        compareValueColumn.setCellFactory {
            object : TableCell<LedConfig, String>() {
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

                            // Style based on value type
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
        }
        compareValueColumn.setOnEditCommit { event ->
            val ledConfig = event.rowValue
            ledConfig.compareValue = event.newValue
        }

        // Make the table editable
        ledTableView.isEditable = true
        ledTableView.items = ledConfigs

        // Load LED configurations
        loadLedConfigurations()
    }

    private fun loadLedConfigurations() {
        ledConfigs.clear()
        ledConfigs.addAll(configService.loadLedConfigurations())
    }

    @FXML
    fun onRefreshComPorts() {
        refreshComPorts()
    }

    @FXML
    fun onConnect() {
        val selectedPort = comPortComboBox.value
        val baudRate = baudRateComboBox.value

        if (selectedPort != null && baudRate != null) {
            try {
                serialCommunication.open(SerialPort.getCommPort(selectedPort), baudRate)
                connectLedsToTables()
                isConnected.set(true)
            } catch (e: Exception) {
                showErrorAlert("Connection Error", "Failed to connect to $selectedPort", e.message ?: "Unknown error")
            }
        } else {
            showErrorAlert("Connection Error", "Invalid Configuration", "Please select a COM port and baud rate.")
        }
    }

    @FXML
    fun onDisconnect() {
        try {
            ledsControl.stop()
            serialCommunication.close()
            isConnected.set(false)
        } catch (e: Exception) {
            showErrorAlert("Disconnection Error", "Failed to disconnect", e.message ?: "Unknown error")
            e.printStackTrace()
        }
    }

    @FXML
    fun onSave() {
        val config = mapOf(
            "comPort" to (comPortComboBox.value ?: ""),
            "baudRate" to (baudRateComboBox.value?.toString() ?: "115200"),
            "autoConnect" to autoConnectCheckBox.isSelected.toString()
        )

        try {
            configService.saveConfiguration(config)
            configService.saveLedConfigurations(ledConfigs)
        } catch (e: Exception) {
            showErrorAlert("Save Error", "Failed to save configuration", e.message ?: "Unknown error")
        }
    }

    @FXML
    fun onCancel() {
        closeWindow()
    }

    private fun refreshComPorts() {
        val ports = getAvailableComPorts()
        comPortComboBox.items = FXCollections.observableArrayList(ports)
        if (ports.isNotEmpty()) {
            comPortComboBox.selectionModel.selectFirst()
        }
    }

    private fun connectLedsToTables() {
        val ledManager = LedManager(serialCommunication, 11)
        ledsControl = LedsControl(ledManager, ledConfigs)
    }

    private fun loadConfiguration() {
        try {
            val config = configService.loadConfiguration()

            // Apply loaded configuration to UI
            config["comPort"]?.let { port ->
                if (comPortComboBox.items.contains(port)) {
                    comPortComboBox.value = port
                }
            }

            config["baudRate"]?.let { baudRate ->
                baudRate.toIntOrNull()?.let { rate ->
                    if (baudRateComboBox.items.contains(rate)) {
                        baudRateComboBox.selectionModel.select(rate)
                    }
                }
            }

            config["autoConnect"]?.let { autoConnect ->
                autoConnectCheckBox.isSelected = autoConnect.toBoolean()

                // Auto-connect if enabled
                if (autoConnectCheckBox.isSelected && comPortComboBox.value != null) {
                    Platform.runLater { onConnect() }
                }
            }
        } catch (e: Exception) {
            // Just log the error, don't show alert as this is during initialization
            println("Failed to load configuration: ${e.message}")
        }
    }

    private fun showErrorAlert(title: String, header: String, content: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.headerText = header
        alert.contentText = content
        alert.showAndWait()
    }

    private fun closeWindow() {
        if (isConnected.get()) {
            onDisconnect()
        }
        val stage = saveButton.scene.window as Stage
        stage.close()
    }
}

