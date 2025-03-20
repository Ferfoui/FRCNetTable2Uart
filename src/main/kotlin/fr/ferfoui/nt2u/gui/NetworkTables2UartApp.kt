package fr.ferfoui.nt2u.gui

import fr.ferfoui.nt2u.networktable.loadNetworkTableLibrairies
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class NetworkTables2UartApp : Application() {

    override fun start(stage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("/fxml/ConfigWindow.fxml"))
        val root = loader.load<javafx.scene.Parent>()

        stage.title = "NetTables2UART Configuration"
        stage.scene = Scene(root, 600.0, 500.0)
        stage.scene.stylesheets.add(javaClass.getResource("/css/styles.css")!!.toExternalForm())
        stage.isResizable = true  // Changed to true to allow resizing
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            loadNetworkTableLibrairies()
            launch(NetworkTables2UartApp::class.java)
        }
    }
}

