plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project.core.asmTree)
}
