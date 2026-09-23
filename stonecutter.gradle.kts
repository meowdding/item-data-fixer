import org.gradle.api.publish.internal.component.DefaultAdhocSoftwareComponent
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.14-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.14-SNAPSHOT" apply false
    `maven-publish`
}
stonecutter active "26.3"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
}

evaluationDependsOnChildren()

//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val dataFixerComponent = componentFactory.adhoc("sbapi")
val minecraftVersionAttribute = Attribute.of("net.minecraft.version", String::class.java)
val remappedAttribute = Attribute.of("net.fabricmc.remapped", String::class.java)

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)

    val java = project.components.getByName<DefaultAdhocSoftwareComponent>("java")
    java.usages.forEach { context ->
        val config = configurations.create(gradleFriendlyVersion + "-" + context.name) {
            isCanBeResolved = false
            isCanBeConsumed = true

            attributes.addAllLater(context.attributes)
            outgoing.artifacts.addAll(context.artifacts)
            dependencies.addAll(context.dependencies)
            dependencyConstraints.addAll(context.dependencyConstraints)

            outgoing.capability("me.owdding:item-data-fixer-$version:${rootProject.version}")
            outgoing.capability("me.owdding:item-data-fixer:${rootProject.version}")
        }
        dataFixerComponent.addVariantsFromConfiguration(config) {
            mapToOptional()
        }
    }
}

publishing {
    publications {
        create("item-data-fixer", MavenPublication::class.java) {
            from(dataFixerComponent)
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
    }
}
//</editor-fold>
