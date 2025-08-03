import com.google.devtools.ksp.gradle.KspTask
import earth.terrarium.cloche.api.target.CommonTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.terrarium.cloche)
    `maven-publish`
}

repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    maven(url = "https://maven.teamresourceful.com/repository/msrandom/")
    mavenCentral()
}

cloche {
    metadata {
        name = "Meowdding Item Dfu"
        description = "A library to convert items from legacy (1.8.9) format to modern."
        modId = "meowdding-item-dfu"
        license = ""
    }

    val root = common {
        withPublication()
    }
    val v4325 = common("4325") {
        withPublication()
        dependsOn(root)
    }

    fun fabric(
        name: String,
        parent: CommonTarget,
        minecraftVersion: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
    ) {
        fabric("fabric:$name") {
            dependsOn(parent)
            this.minecraftVersion = minecraftVersion
            this.loaderVersion = loaderVersion

            metadata {
                dependency {
                    modId = "fabricloader"
                    required = true
                }
            }
        }
    }

    fabric("1.21.5", v4325)
    fabric("1.21.8", v4325)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType(KspTask::class).configureEach { enabled = false }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("item-data-fixer")
                url.set("https://github.com/meowdding/item-data-fixer")

                scm {
                    connection.set("https://github.com/meowdding/item-data-fixer.git")
                    developerConnection.set("git:https://github.com/meowdding/item-data-fixer.git")
                    url.set("https://github.com/meowdding/item-data-fixer")
                }
            }
        }
    }
    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/thatgravyboat/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
            }
        }
    }
}

artifacts {
    add("fabric1215RuntimeElements", tasks["fabric1215JarInJar"])
    add("fabric1218RuntimeElements", tasks["fabric1218JarInJar"])
}
