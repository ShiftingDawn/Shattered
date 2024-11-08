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

    implementation(project(":lib"))
    implementation(project.libs.annotations)
    implementation(project.libs.fastutil)
    implementation(project.libs.gson)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
