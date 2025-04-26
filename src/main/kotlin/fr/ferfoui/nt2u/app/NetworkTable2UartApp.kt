package fr.ferfoui.nt2u.app

import fr.ferfoui.nt2u.APP_NAME
import fr.ferfoui.nt2u.CSS_PATH
import fr.ferfoui.nt2u.FXML_CONFIG_PATH
import fr.ferfoui.nt2u.ICON_PATH
import fr.ferfoui.nt2u.gui.AppConfigController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import java.awt.Taskbar
import java.awt.Toolkit

class NetworkTable2UartApp : Application() {

    override fun start(stage: Stage) {
        val loader = FXMLLoader(javaClass.getResource(FXML_CONFIG_PATH))
        val root = loader.load<javafx.scene.Parent>()

        val appIcon = Image(ICON_PATH)
        stage.icons.add(appIcon)

        // Set icon on the taskbar
        if (Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()

            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                val defaultToolkit = Toolkit.getDefaultToolkit()
                val dockIcon = defaultToolkit.getImage(javaClass.getResource(ICON_PATH));
                taskbar.setIconImage(dockIcon);
            }
        }

        // Set on close request
        val controller = loader.getController<AppConfigController>()
        stage.setOnCloseRequest(controller::onCloseRequest)

        stage.title = APP_NAME
        stage.scene = Scene(root, 800.0, 500.0)
        stage.scene.stylesheets.add(javaClass.getResource(CSS_PATH)!!.toExternalForm())
        stage.isResizable = true
        stage.show()
    }
}

