package fr.ferfoui.nt2u.gui

import com.fazecast.jSerialComm.SerialPort
import fr.ferfoui.nt2u.LED_COUNT
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
import javafx.stage.FileChooser
import javafx.stage.WindowEvent
import java.io.File

/**
 * Controller class for the application configuration window.
 * This class handles the UI interactions and manages the application configuration.
 */
class AppConfigController {

    @FXML private lateinit var comPortComboBox: ComboBox<String>
    @FXML private lateinit var baudRateComboBox: ComboBox<Int>
    @FXML private lateinit var connectionStatusCircle: Circle
    @FXML private lateinit var connectionStatusLabel: Label
    @FXML private lateinit var connectButton: Button
    @FXML private lateinit var disconnectButton: Button
    @FXML private lateinit var saveButton: Button
    @FXML private lateinit var loadButton: Button
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

    /**
     * Initialize the controller after the FXML file has been loaded.
     * This method is called by the FXMLLoader when the FXML file is loaded.
     */
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

    /**
     * Initialize the LED table with columns and cell factories.
     */
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
        conditionColumn.setCellFactory { ConditionTableCell() }
        conditionColumn.setOnEditCommit { event ->
            val ledConfig = event.rowValue
            ledConfig.condition = event.newValue
        }

        // Set up compare value column with type validation
        compareValueColumn.cellValueFactory = PropertyValueFactory("compareValue")
        compareValueColumn.setCellFactory { CompareValueTableCell(compareValueColumn) }
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


    /**
     * Handle the close request event for the application window.
     * This method is called in the application's main class to set the close request action.
     */
    @FXML
    fun onCloseRequest(event: WindowEvent) {
        if (isConnected.get()) {
            onDisconnect()
        }
    }

    /**
     * Handle the refresh button click event to refresh the available COM ports.
     */
    @FXML
    fun onRefreshComPorts() {
        refreshComPorts()
    }

    /**
     * Handle the connect button click event to establish a connection to the selected COM port and to smartdashboard.
     */
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

    /**
     * Handle the disconnect button click event to close the connection to the COM port and smartdashboard.
     */
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

    /**
     * Handle the save button click event to save the current configuration to a file.
     */
    @FXML
    fun onSave() {
        val config = mapOf(
            "comPort" to (comPortComboBox.value ?: ""),
            "baudRate" to (baudRateComboBox.value?.toString() ?: "115200"),
            "autoConnect" to autoConnectCheckBox.isSelected.toString()
        )

        try {
            configService.saveConfiguration(config)
            configService.saveLedConfigurations(ledConfigs, config)
        } catch (e: Exception) {
            showErrorAlert("Save Error", "Failed to save configuration", e.message ?: "Unknown error")
        }
    }

    /**
     * Handle the load button click event to load the configuration from an external file.
     */
    @FXML
    fun onLoadFromFile() {
        val file = FileChooser().showOpenDialog(loadButton.scene.window)
        if (file != null) {
            loadConfiguration(file)
            loadLedConfigurations(file)
        }
    }

    /**
     * Get the available COM ports from the system.
     */
    private fun refreshComPorts() {
        val ports = getAvailableComPorts()
        comPortComboBox.items = FXCollections.observableArrayList(ports)
        if (ports.isNotEmpty()) {
            comPortComboBox.selectionModel.selectFirst()
        }
    }

    /**
     * Connect the LEDs to the tables using the [LedManager] and [LedsControl] classes.
     */
    private fun connectLedsToTables() {
        val ledManager = LedManager(serialCommunication, LED_COUNT)
        ledsControl = LedsControl(ledManager, ledConfigs)
    }

    /**
     * Load the configuration from a file or the default location.
     * If a file is provided, it will be used to load the configuration.
     *
     * @param configFile The file to load the configuration from. If null, the default location will be used.
     */
    private fun loadConfiguration(configFile: File? = null) {
        try {
            val config =
                if (configFile != null)
                    configService.loadConfiguration(configFile)
                else
                    configService.loadConfiguration()

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

    /**
     * Load the LED configurations from a file or the default location.
     * If a file is provided, it will be used to load the configurations.
     *
     * @param file The file to load the LED configurations from. If null, the default location will be used.
     */
    private fun loadLedConfigurations(file: File? = null) {
        ledConfigs.clear()
        val configs =
            if (file != null) {
                configService.loadLedConfigurations(file)
            } else {
                configService.loadLedConfigurations()
            }
        ledConfigs.addAll(configs)
    }

    /**
     * Show an error alert dialog with the specified title, header, and content.
     *
     * @param title The title of the alert dialog.
     * @param header The header text of the alert dialog.
     * @param content The content text of the alert dialog.
     */
    private fun showErrorAlert(title: String, header: String, content: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.headerText = header
        alert.contentText = content
        alert.showAndWait()
    }
}

