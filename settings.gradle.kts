pluginManagement {
    repositories {
        maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
        maven(url = "https://maven.teamresourceful.com/repository/msrandom/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "item-data-fixer"