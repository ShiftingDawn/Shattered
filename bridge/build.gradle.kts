plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project.core.asmTree)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}