plugins {
    java
    id("io.freefair.lombok") version "8.10.2"
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":bridge"))
    implementation(project.core.asm)
    implementation(project.core.asmTree)
    implementation(project.core.asmUtil)
    implementation(project.core.log4j)

    implementation(project(":lib"))
    implementation(project.libs.annotations)
    implementation(project.libs.fastutil)
    implementation(project.libs.gson)
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
    implementation(project.libs.typetools)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
