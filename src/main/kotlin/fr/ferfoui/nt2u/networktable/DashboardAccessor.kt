package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance

class DashboardAccessor {

    private val smartDashboardTable: NetworkTable

    init {
        val instance = NetworkTableInstance.create()
        instance.setServerTeam(9220)

        smartDashboardTable = instance.getTable("SmartDashboard")
    }

    fun printKeys() {
        smartDashboardTable.keys.forEach {
            println(it)
        }
    }

}