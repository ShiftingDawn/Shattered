plugins {
    application
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project.core.asm)
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
