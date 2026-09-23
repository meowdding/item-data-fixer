pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("dev.kikugie.stonecutter") version "0.7.10"
}

rootProject.name = "item-data-fixer"

val versions = listOf("26.1", "26.2", "26.3", "26.4")

stonecutter {
    create(rootProject) {
        versions.forEach(::version)
        vcsVersion = versions.last()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        versions.forEach {
            val name = it.replace(".", "")
            create("libs$name") {
                from(
                    files(
                        rootProject.projectDir.resolve("gradle/${it.replace(".", "_")}.versions.toml")
                    )
                )
            }
        }
    }
}