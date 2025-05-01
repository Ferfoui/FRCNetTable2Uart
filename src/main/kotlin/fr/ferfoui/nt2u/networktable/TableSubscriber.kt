package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.*
import java.util.*

// To use NetworkTables:
// https://docs.wpilib.org/en/stable/docs/software/networktables/client-side-program.html

/**
 * Class to access a NetworkTable and subscribe to its topics.
 *
 * @param instance The [NetworkTableInstance] to use.
 * @param tableName The name of the table to access.
 */
class TableSubscriber(private val instance: NetworkTableInstance, val tableName: String) {

    private val networkTable: NetworkTable = instance.getTable(tableName)

    private val subscribers = mutableMapOf<String, Subscriber>()
    private val listenerHandles = mutableListOf<Int>()

    /**
     * Subscribe to a [String] topic with a callback.
     *
     * The callback will be called when the value changes and when the subscription is created.
     *
     * @param topic The topic to subscribe to.
     * @param callback The callback to call when the value changes.
     */
    fun subscribeToString(topic: String, callback: (String) -> Unit) {
        val subscriber = networkTable.getStringTopic(topic).subscribe("")

        val listenerHandle = instance.addListener(
            subscriber,
            EnumSet.of(NetworkTableEvent.Kind.kValueAll)
        ) { _ ->
            callback((subscribers[topic] as StringSubscriber).get())
        }

        callback(subscriber.get())

        subscribers[topic] = subscriber
        listenerHandles.add(listenerHandle)
    }

    /**
     * Subscribe to a boolean topic with a callback.
     *
     * The callback will be called when the value changes and when the subscription is created.
     *
     * @param topic The topic to subscribe to.
     * @param callback The callback to call when the value changes.
     */
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

    /**
     * Subscribe to an integer topic with a callback.
     *
     * The callback will be called when the value changes and when the subscription is created.
     *
     * @param topic The topic to subscribe to.
     * @param callback The callback to call when the value changes.
     */
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

    /**
     * Subscribe to a double topic with a callback.
     *
     * The callback will be called when the value changes and when the subscription is created.
     *
     * @param topic The topic to subscribe to.
     * @param callback The callback to call when the value changes.
     */
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

    // TODO: Remove this method
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

    /**
     * Close all listeners and stop the client.
     */
    fun close() {
        listenerHandles.forEach { instance.removeListener(it) }
        instance.stopClient()
    }

}