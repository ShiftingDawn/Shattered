plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Core
val asm = "9.7.1"
val log4j = "2.24.1"

// Libs
val lwjgl = "3.3.4"
val joml = "1.10.7"
val annotations = "26.0.1"
val fastutil = "8.5.15"
val gson = "2.11.0"
val typetools = "0.6.3"

dependencyResolutionManagement {
    versionCatalogs.create("core") {
        val asmVersion = version("asm", asm)
        library("asm", "org.ow2.asm", "asm").versionRef(asmVersion)
        library("asmTree", "org.ow2.asm", "asm-tree").versionRef(asmVersion)
        library("asmUtil", "org.ow2.asm", "asm-util").versionRef(asmVersion)
        library("log4j", "org.apache.logging.log4j", "log4j-core").versionRef(version("log4j", log4j))
    }
    versionCatalogs.create("libs") {
        library("annotations", "org.jetbrains", "annotations").versionRef(version("annotations", annotations))
        library("fastutil", "it.unimi.dsi", "fastutil").versionRef(version("fastutil", fastutil))
        library("gson", "com.google.code.gson", "gson").versionRef(version("gson", gson))
        library("lwjgl", "org.lwjgl", "lwjgl-bom").versionRef(version("lwjgl", lwjgl))
        library("joml", "org.joml", "joml").versionRef(version("joml", joml));
        library("typetools", "net.jodah", "typetools").versionRef(version("typetools", typetools))
    }
}

rootProject.name = "MultiShattered"
include("bridge")
include("bootstrap")
include("lib")
include("core")
