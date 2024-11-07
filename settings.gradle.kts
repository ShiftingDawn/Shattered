plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Core
val asm = "9.7.1"

// Libs
val fastutil = "8.5.15"
val gson = "2.11.0"

dependencyResolutionManagement {
    versionCatalogs.create("core") {
        library("asm", "org.ow2.asm", "asm").versionRef(version("asm", asm))
    }
    versionCatalogs.create("libs") {
        library("fastutil", "it.unimi.dsi", "fastutil").versionRef(version("fastutil", fastutil))
        library("gson", "com.google.code.gson", "gson").versionRef(version("gson", gson))
    }
}

rootProject.name = "MultiShattered"
include("bootstrap")
include("core")
