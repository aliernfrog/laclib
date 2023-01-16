plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

afterEvaluate {
    publishing {
        publications {
            register("java", MavenPublication::class) {
                from(components["java"])
            }
        }
    }
}