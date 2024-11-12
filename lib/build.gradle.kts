plugins {
    java
    id("io.freefair.lombok") version "8.10.2"
}

dependencies {
    implementation(project.libs.annotations)
    implementation(platform(project.libs.lwjgl))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation(project.libs.joml)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
