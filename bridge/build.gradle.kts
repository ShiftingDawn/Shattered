plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project.core.asmTree)
    compileOnly(project.core.asmUtil)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
