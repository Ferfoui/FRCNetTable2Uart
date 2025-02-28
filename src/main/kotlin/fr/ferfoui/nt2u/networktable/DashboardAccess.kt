package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance

class DashboardAccess {

    private val smartDashboardTable: NetworkTable

    init {
        val instance = NetworkTableInstance.getDefault()
        instance.setServerTeam(9220)

        smartDashboardTable = instance.getTable("SmartDashboard")


    }


}