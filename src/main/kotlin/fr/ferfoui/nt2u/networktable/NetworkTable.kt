package fr.ferfoui.nt2u.networktable

import edu.wpi.first.math.jni.WPIMathJNI
import edu.wpi.first.networktables.NetworkTablesJNI
import edu.wpi.first.util.CombinedRuntimeLoader
import edu.wpi.first.util.WPIUtilJNI
import fr.ferfoui.nt2u.app.NetworkTable2UartApp


fun loadNetworkTableLibrairies() {
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false)
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false)
    WPIMathJNI.Helper.setExtractOnStaticLoad(false)
    CombinedRuntimeLoader.loadLibraries(
        NetworkTable2UartApp::class.java, "wpiutiljni", "wpimathjni", "ntcorejni"
    )
}