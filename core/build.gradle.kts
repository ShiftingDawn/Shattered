plugins {
    java
    id("io.freefair.lombok") version "8.10.2"
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project.libs.fastutil)
    implementation(project.libs.gson)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Checkstyle>().configureEach {
    configDirectory.set(layout.projectDirectory.dir("gradle"))
    configFile = file("$rootDir/gradle/checkstyle.xml")
}
