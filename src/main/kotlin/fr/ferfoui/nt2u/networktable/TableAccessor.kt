package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.BooleanSubscriber
import edu.wpi.first.networktables.DoubleSubscriber
import edu.wpi.first.networktables.IntegerSubscriber
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableEvent
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.networktables.StringSubscriber
import edu.wpi.first.networktables.Subscriber
import fr.ferfoui.nt2u.SMARTDASHBOARD_NAME
import java.util.*

// To use NetworkTables:
// https://docs.wpilib.org/en/stable/docs/software/networktables/client-side-program.html

class TableAccessor(private val instance: NetworkTableInstance, tableName: String = SMARTDASHBOARD_NAME) {

    private val networkTable: NetworkTable = instance.getTable(tableName)

    private val subscribers = mutableMapOf<String, Subscriber>()
    private val listenerHandles = mutableListOf<Int>()

    fun subscribeToString(topic: String, callback: (String) -> Unit) {
        val subscriber = networkTable.getStringTopic(topic).subscribe("")

        val listenerHandle = instance.addListener(subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { _ ->
            callback((subscribers[topic] as StringSubscriber).get())
        }

        callback(subscriber.get())

        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
    }

    fun subscribeToBoolean(topic: String, callback: (Boolean) -> Unit) {
        val subscriber = networkTable.getBooleanTopic(topic).subscribe(false)

        val listenerHandle = instance.addListener(
            subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { _ ->
            callback((subscribers[topic] as BooleanSubscriber).get())
        }

        callback(subscriber.get())
        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
    }

    fun subscribeToInteger(topic: String, callback: (Int) -> Unit) {
        val subscriber = networkTable.getIntegerTopic(topic).subscribe(0)

        val listenerHandle = instance.addListener(
            subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { _ ->
            callback((subscribers[topic] as IntegerSubscriber).get().toInt())
        }

        callback(subscriber.get().toInt())
        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
    }

    fun subscribeToDouble(topic: String, callback: (Double) -> Unit) {
        val subscriber = networkTable.getDoubleTopic(topic).subscribe(0.0)

        val listenerHandle = instance.addListener(
            subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { _ ->
            callback((subscribers[topic] as DoubleSubscriber).get())
        }

        callback(subscriber.get())
        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
    }

    fun subscribeTest() {
        val hello = networkTable.getStringTopic("hello").subscribe("default")
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