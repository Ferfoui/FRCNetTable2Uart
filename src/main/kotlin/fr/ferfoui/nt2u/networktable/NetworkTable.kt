package fr.ferfoui.nt2u.networktable

import edu.wpi.first.math.jni.WPIMathJNI
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.NetworkTablesJNI
import edu.wpi.first.util.CombinedRuntimeLoader
import edu.wpi.first.util.WPIUtilJNI
import fr.ferfoui.nt2u.NETWORK_TABLE_USER_NAME
import fr.ferfoui.nt2u.NT_CORE_JNI
import fr.ferfoui.nt2u.TEAM_NUMBER
import fr.ferfoui.nt2u.WPI_MATH_JNI
import fr.ferfoui.nt2u.WPI_UTIL_JNI

/**
 * Loads the required wpi libraries for NetworkTables.
 *
 * @param classToLoad The class used to load the libraries.
 */
fun loadNetworkTableLibrairies(classToLoad: Class<*>) {
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false)
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false)
    WPIMathJNI.Helper.setExtractOnStaticLoad(false)
    CombinedRuntimeLoader.loadLibraries(
        classToLoad, WPI_UTIL_JNI, WPI_MATH_JNI, NT_CORE_JNI
    )
}

/**
 * Initializes the NetworkTableInstance with the default settings.
 * This includes starting the client and setting the server team.
 *
 * @return The initialized NetworkTableInstance.
 */
fun initializeNetworkTableInstance() : NetworkTableInstance {
    val instance = NetworkTableInstance.getDefault()

    instance.startClient4(NETWORK_TABLE_USER_NAME)
    instance.setServerTeam(TEAM_NUMBER)
    instance.startDSClient()
    return instance
}