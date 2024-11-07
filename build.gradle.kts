plugins {
    application
    id("io.freefair.lombok") version "8.10.2"
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "Main"
}

tasks.withType<Checkstyle>().configureEach {
    configDirectory.set(layout.projectDirectory.dir("gradle"))
    configFile = file("$rootDir/gradle/checkstyle.xml")
}
