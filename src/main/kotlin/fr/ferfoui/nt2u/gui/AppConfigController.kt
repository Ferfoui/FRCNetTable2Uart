package fr.ferfoui.nt2u.gui

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.ApplicationConfigurationService
import fr.ferfoui.nt2u.model.LedConfig
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

    private val ledConfigs: ObservableList<LedConfig> = FXCollections.observableArrayList()

    private val configService = ApplicationConfigurationService()
    
    private val isConnected = SimpleBooleanProperty(false)
    private val statusText = SimpleStringProperty("Not Connected")

    private lateinit var serialCommunication: SerialCommunication
    
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
                serialCommunication = SerialCommunication(SerialPort.getCommPort(selectedPort), baudRate)
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
            serialCommunication.close()
            isConnected.set(false)
        } catch (e: Exception) {
            showErrorAlert("Disconnection Error", "Failed to disconnect", e.message ?: "Unknown error")
        }
    }
    
    @FXML
    fun onSave() {
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
                closeWindow()
            } catch (e: Exception) {
                showErrorAlert("Save Error", "Failed to save configuration", e.message ?: "Unknown error")
            }
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
        val stage = saveButton.scene.window as Stage
        stage.close()
    }
}

