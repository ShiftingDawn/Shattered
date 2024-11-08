plugins {
    java
    id("io.freefair.lombok") version "8.10.2"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":bridge"))

    implementation(project.libs.annotations)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}