package fr.ferfoui.nt2u.networktable

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

const val NETWORK_TABLE_NAME = "test"
const val CLIENT_TABLE_NAME = "test client"
const val TEAM_NUMBER = 9220

class TableSubscriberTest {

    init {
        loadNetworkTableLibrairies(TableSubscriberTest::class.java)
    }

    private lateinit var networkTableInstance: NetworkTableInstance

    private lateinit var serverNetworkTable: NetworkTable
    private lateinit var tableSubscriber: TableSubscriber

    @BeforeEach
    fun setUp() {
        networkTableInstance = NetworkTableInstance.getDefault()

        networkTableInstance.startServer("")
        // The empty string is used to start the server without persisting the data
        networkTableInstance.setServerTeam(TEAM_NUMBER)
        serverNetworkTable = networkTableInstance.getTable(NETWORK_TABLE_NAME)
        println("test : NTServer started")

        networkTableInstance.startClient4(CLIENT_TABLE_NAME)
        networkTableInstance.startDSClient()
        tableSubscriber = TableSubscriber(networkTableInstance, NETWORK_TABLE_NAME)
        println("test : NTClient started")
    }

    @AfterEach
    fun tearDown() {
        tableSubscriber.close()
        networkTableInstance.stopClient()
        networkTableInstance.stopServer()
        println("test : Server and client stopped")
    }

    @Test
    fun subscribeToString() {
        val topic = "testString"
        val expectedValue = "Hello World"

        val publisher = serverNetworkTable.getStringTopic(topic).publish()
        publisher.set(expectedValue)

        // Subscribe to the topic
        var obtainedValue: String? = null
        tableSubscriber.subscribeToString(topic) { value ->
            obtainedValue = value
        }

        // Test the initial value
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == expectedValue) { "Expected $expectedValue but got $obtainedValue" }

        // Test the updated value
        val newValue = "New Value"
        publisher.set(newValue)
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == newValue) { "Expected $newValue but got $obtainedValue" }
    }

    @Test
    fun subscribeToBoolean() {
        val topic = "testBoolean"
        val expectedValue = true

        val publisher = serverNetworkTable.getBooleanTopic(topic).publish()
        publisher.set(expectedValue)

        // Subscribe to the topic
        var obtainedValue: Boolean? = null
        tableSubscriber.subscribeToBoolean(topic) { value ->
            obtainedValue = value
        }

        // Test the initial value
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == expectedValue) { "Expected $expectedValue but got $obtainedValue" }

        // Test the updated value
        val newValue = false
        publisher.set(newValue)
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == newValue) { "Expected $newValue but got $obtainedValue" }
    }

    @Test
    fun subscribeToInteger() {
        val topic = "testInteger"
        val expectedValue = 42

        val publisher = serverNetworkTable.getIntegerTopic(topic).publish()
        publisher.set(expectedValue.toLong())

        // Subscribe to the topic
        var obtainedValue: Int? = null
        tableSubscriber.subscribeToInteger(topic) { value ->
            obtainedValue = value
        }

        // Test the initial value
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == expectedValue) { "Expected $expectedValue but got $obtainedValue" }

        // Test the updated value
        val newValue = 100
        publisher.set(newValue.toLong())
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == newValue) { "Expected $newValue but got $obtainedValue" }
    }

    @Test
    fun subscribeToDouble() {
        val topic = "testDouble"
        val expectedValue = 3.14

        val publisher = serverNetworkTable.getDoubleTopic(topic).publish()
        publisher.set(expectedValue)

        // Subscribe to the topic
        var obtainedValue: Double? = null
        tableSubscriber.subscribeToDouble(topic) { value ->
            obtainedValue = value
        }

        // Test the initial value
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == expectedValue) { "Expected $expectedValue but got $obtainedValue" }

        // Test the updated value
        val newValue = 2.718
        publisher.set(newValue)
        Thread.sleep(300) // Wait for the callback to be called
        assert(obtainedValue == newValue) { "Expected $newValue but got $obtainedValue" }
    }

}