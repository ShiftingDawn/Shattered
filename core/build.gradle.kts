plugins {
    application
    id("io.freefair.lombok") version "8.10.2"
    checkstyle
}

dependencies {
    implementation(project(":lib"))
    implementation(project.libs.asm)
    implementation(project.libs.log4j)
    implementation(project.libs.annotations)
    implementation(project.libs.fastutil)
    implementation(project.libs.gson)
    implementation(project.libs.typetools)
    implementation(platform(project.libs.lwjgl))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    val lwjglNatives = "natives-windows"
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation(project.libs.joml)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "Main"

    applicationDefaultJvmArgs = listOf(
        "-Dshattered.log.level=TRACE",
        "-Dshattered.workspace.root=$rootDir/run",
        "-Dshattered.eventbus.dumpclasses=true"
    )
}
