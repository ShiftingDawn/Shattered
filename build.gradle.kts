plugins {
    idea
    checkstyle
}

idea {
    module {
        isDownloadJavadoc = true;
        isDownloadSources = true;
    }
}

tasks.withType<JavaCompile> {
    options.setIncremental(true)
}

tasks.withType<Checkstyle>().configureEach {
    configDirectory.set(layout.projectDirectory.dir("gradle"))
    configFile = file("$rootDir/gradle/checkstyle.xml")
}
