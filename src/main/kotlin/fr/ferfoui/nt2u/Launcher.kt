package fr.ferfoui.nt2u

import fr.ferfoui.nt2u.app.NetworkTable2UartApp
import fr.ferfoui.nt2u.networktable.loadNetworkTableLibrairies
import javafx.application.Application

/**
 * Launcher class that serves as the main entry point for the application.
 * This is needed to properly handle JavaFX when packaged as a fat JAR.
 */
object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        val appClass = NetworkTable2UartApp::class.java

        loadNetworkTableLibrairies(appClass)
        Application.launch(appClass)
    }
}

