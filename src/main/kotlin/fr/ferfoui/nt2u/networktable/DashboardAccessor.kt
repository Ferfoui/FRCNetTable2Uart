package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance

// To use NetworkTables:
// https://docs.wpilib.org/en/stable/docs/software/networktables/client-side-program.html

class DashboardAccessor {

    private val smartDashboardTable: NetworkTable

    init {
        val instance = NetworkTableInstance.getDefault()

        smartDashboardTable = instance.getTable("SmartDashboard")

        instance.startClient4("OperatorConsole")
        instance.setServerTeam(9220)
        instance.startDSClient()
    }

    fun subscribeTest() {
        val hello = smartDashboardTable.getStringTopic("hello").subscribe("default")
        while (true) {
            try {
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                println("interrupted")
                return
            }
            println(hello.get())
        }
    }

}