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
    implementation(project.core.log4j)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "Main"

    applicationDefaultJvmArgs = listOf(
        "-Dshattered.workspace.root=$rootDir/run",
        "-Dshattered.bootstrap.dumpclasses=true",
        "-Dshattered.eventbus.dumpclasses=true"
    )
}
