package fr.ferfoui.nt2u.app

import fr.ferfoui.nt2u.networktable.loadNetworkTableLibrairies
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class NetworkTable2UartApp : Application() {

    override fun start(stage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("/fxml/ConfigWindow.fxml"))
        val root = loader.load<javafx.scene.Parent>()

        stage.title = "FRCNetTable2UART"
        stage.scene = Scene(root, 800.0, 500.0)
        stage.scene.stylesheets.add(javaClass.getResource("/css/styles.css")!!.toExternalForm())
        stage.isResizable = true
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            loadNetworkTableLibrairies()
            launch(NetworkTable2UartApp::class.java)
        }
    }
}

