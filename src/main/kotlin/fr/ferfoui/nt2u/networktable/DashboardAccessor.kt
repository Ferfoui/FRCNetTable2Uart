package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableEvent
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.Subscriber
import java.util.*

// To use NetworkTables:
// https://docs.wpilib.org/en/stable/docs/software/networktables/client-side-program.html

class DashboardAccessor {

    private val instance = NetworkTableInstance.getDefault()
    private val smartDashboardTable: NetworkTable = instance.getTable("SmartDashboard")

    private val subscribers = mutableMapOf<String, Subscriber>()

    private val listenerHandles = mutableListOf<Int>()

    init {
        instance.startClient4("operatorconsole")
        instance.setServerTeam(9220)
        instance.startDSClient()
    }

    fun subscribeToString(topic: String, callback: (String) -> Unit) {
        val subscriber = smartDashboardTable.getStringTopic(topic).subscribe("true")

        val listenerHandle = instance.addListener(subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { event ->
            callback(event.valueData.toString())
        }

        callback(subscriber.get())

        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
    }

    fun subscribeToBoolean(topic: String, callback: (Boolean) -> Unit) {
        val subscriber = smartDashboardTable.getBooleanTopic(topic).subscribe(false)

        val listenerHandle = instance.addListener(
            subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { event ->
            callback(subscriber.get())
        }

        callback(subscriber.get())
        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
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

    fun close() {
        listenerHandles.forEach { instance.removeListener(it) }
        instance.stopClient()
    }

}