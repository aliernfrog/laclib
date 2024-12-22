import java.net.URI

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

afterEvaluate {
    publishing {
        publications {
            register("java", MavenPublication::class) {
                from(components["java"])
            }
        }
    }
        
    val githubPackagesURL = System.getenv("GITHUB_PACKAGES_URL")

    if (
        !System.getenv("GITHUB_TOKEN").isNullOrEmpty()
        && !githubPackagesURL.isNullOrEmpty()
    ) repositories {
        maven {
            name = "GitHubPackages"
            url = URI(githubPackagesURL)
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}