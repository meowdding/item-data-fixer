plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    `maven-publish`
}
stonecutter active "1.21.11"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    replacements.string("identifier") {
        direction = eval(current.version, "<1.21.11")
        replace("import net.minecraft.resources.Identifier", "import net.minecraft.resources.ResourceLocation as Identifier")
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            stonecutter.versions.forEach {
                val child = project(it.project)
                child.afterEvaluate {
                    artifact(child.tasks.named("remapJar"))
                    artifact(child.tasks.named("remapSourcesJar"))
                }
            }

            version = project.version.toString()

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
        flatDir {
            name = "meow"
            dirs = setOf(rootProject.layout.projectDirectory.dir("test").asFile)
        }
    }
}
