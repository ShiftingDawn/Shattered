plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":bridge"))
    runtimeOnly(project(":core"))
    implementation(project.core.asm)
    implementation(project.core.asmTree)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "Main"

    applicationDefaultJvmArgs = listOf(
        "-Dshattered.bootstrap.dumpclasses=true",
        "-Dshattered.eventbus.dumpclasses=true"
    )

    val runDir = File("$rootDir/run")
    runDir.mkdirs()
    tasks.run.get().workingDir = runDir
}