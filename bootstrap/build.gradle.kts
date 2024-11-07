plugins {
    application
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":lib"))
    runtimeOnly(project(":core"))
    implementation(project.core.asm)
    implementation(project.core.asmTree)
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
