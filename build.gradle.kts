plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}