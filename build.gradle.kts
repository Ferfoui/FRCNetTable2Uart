import edu.wpi.first.tools.WpilibToolsExtension

plugins {
    kotlin("jvm") version libs.versions.kotlin
    id("edu.wpi.first.GradleRIO") version libs.versions.gradle.rio
    id("edu.wpi.first.WpilibTools") version libs.versions.wpilib.tools
}

group = "fr.ferfoui.nt2u"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

wpilibTools.deps.setWpilibVersion(wpi.versions.wpilibVersion.get())

val nativeConfigName = "wpilibNatives"
val nativeConfig = configurations.create(nativeConfigName)

val nativeTasks: WpilibToolsExtension.NewTaskSet = wpilibTools.createExtractionTasks {
    configurationName = nativeConfigName
}

nativeTasks.addToSourceSetResources(sourceSets["main"])
nativeConfig.dependencies.add(wpilibTools.deps.wpilib("ntcore"))

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines)

    implementation(libs.jserialcomm)

    implementation(wpilibTools.deps.wpilibJava("ntcore"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}