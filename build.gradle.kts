import edu.wpi.first.tools.WpilibToolsExtension
import org.gradle.api.tasks.bundling.Zip

plugins {
    kotlin("jvm") version libs.versions.kotlin
    application
    alias(libs.plugins.gradle.shadow)
    alias(libs.plugins.gradle.rio)
    alias(libs.plugins.wpilib.tools)
    alias(libs.plugins.javafx)
}

group = "fr.ferfoui.nt2u"
version = "0.3"

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
nativeConfig.dependencies.add(wpilibTools.deps.wpilib("wpimath"))
nativeConfig.dependencies.add(wpilibTools.deps.wpilib("wpinet"))
nativeConfig.dependencies.add(wpilibTools.deps.wpilib("wpiutil"))
nativeConfig.dependencies.add(wpilibTools.deps.wpilib("ntcore"))

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines)

    implementation(libs.jserialcomm)

    implementation(wpilibTools.deps.wpilibJava("wpiutil"))
    implementation(wpilibTools.deps.wpilibJava("wpimath"))
    implementation(wpilibTools.deps.wpilibJava("wpinet"))
    implementation(wpilibTools.deps.wpilibJava("ntcore"))

    implementation("com.fasterxml.jackson.core:jackson-annotations:${wpi.versions.jacksonVersion.get()}")
    implementation("com.fasterxml.jackson.core:jackson-core:${wpi.versions.jacksonVersion.get()}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${wpi.versions.jacksonVersion.get()}")

    implementation("org.ejml:ejml-simple:${wpi.versions.ejmlVersion.get()}")
    implementation("us.hebi.quickbuf:quickbuf-runtime:${wpi.versions.quickbufVersion.get()}")

}

javafx {
    version = "17.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("fr.ferfoui.nt2u.Launcher")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}


val outputDir = layout.buildDirectory.dir("libs/v${project.version}").get().toString()

tasks.jar {
    destinationDirectory.set(file(outputDir))
}

// Configure the Shadow plugin
tasks.shadowJar {
    archiveClassifier.set("all")

    // Ensure JavaFX is properly included
    mergeServiceFiles()

    manifest {
        attributes(mapOf(
            "Main-Class" to "fr.ferfoui.nt2u.Launcher",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        ))
    }

    destinationDirectory.set(file(outputDir))
}

val runBashContent = """
@echo off
start C:\Users\Public\wpilib\2025\jdk\bin\javaw.exe -jar FRCNetTable2Uart-${project.version}-all.jar
""".trimIndent()
val batFileDir = "$outputDir/run.bat"


tasks.register("createRunBash") {
    val runBashFile = file(batFileDir)
    outputs.file(runBashFile)
    doLast {
        runBashFile.writeText(runBashContent)
    }
}

tasks.register<Zip>("packageZip") {
    dependsOn(tasks.shadowJar, "createRunBash")
    from(tasks.shadowJar.get().outputs.files) {
        rename { "FRCNetTable2Uart-${project.version}-all.jar" }
    }
    from(batFileDir)
    archiveFileName.set("FRCNetTable2Uart-${project.version}.zip")
    destinationDirectory.set(file(outputDir))
}

tasks.build {
    dependsOn("packageZip")
}
