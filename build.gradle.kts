plugins {
    kotlin("jvm") version "2.1.0"
}

group = "fr.ferfoui.nt2u"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines)

    implementation(libs.jSerialComm)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}