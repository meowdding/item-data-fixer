import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.14-SNAPSHOT" apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.0" apply false
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

//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val dataFixerComponent = componentFactory.adhoc("sbapi")
val minecraftVersionAttribute = Attribute.of("net.minecraft.version", String::class.java)
val remappedAttribute = Attribute.of("net.fabricmc.remapped", String::class.java)

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)
    val remappedApiElements = configurations.create(gradleFriendlyVersion + "remappedApiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "true")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            outgoing.artifact(tasks.named("remapJar"))
        }

        outgoing.capability("me.owdding:item-data-fixer-$version-remapped:${rootProject.version}")
        outgoing.capability("me.owdding:item-data-fixer:${rootProject.version}")
    }
    val apiElements = configurations.create(gradleFriendlyVersion + "apiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            outgoing.artifact(tasks.named("jar"))
        }

        outgoing.capability("me.owdding:item-data-fixer-$version:${rootProject.version}")
        outgoing.capability("me.owdding:item-data-fixer:${rootProject.version}")
    }

    val remappedRuntimeElements = configurations.create(gradleFriendlyVersion + "remappedRuntimeElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "true")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            outgoing.artifact(tasks.named("remapJar"))
        }

        outgoing.capability("me.owdding:item-data-fixer-$version-remapped:${rootProject.version}")
        outgoing.capability("me.owdding:item-data-fixer:${rootProject.version}")
    }
    val runtimeElements = configurations.create(gradleFriendlyVersion + "runtimeElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            outgoing.artifact(tasks.named("jar"))
        }

        outgoing.capability("me.owdding:item-data-fixer-$version:${rootProject.version}")
        outgoing.capability("me.owdding:item-data-fixer:${rootProject.version}")
    }

    val sourcesElements = configurations.create(gradleFriendlyVersion + "sources") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            outgoing.artifact(tasks.named("sourcesJar"))
        }

        outgoing.capability("me.owdding:item-data-fixer-$version:${rootProject.version}")
        outgoing.capability("me.owdding:item-data-fixer:${rootProject.version}")
    }

    dataFixerComponent.addVariantsFromConfiguration(remappedApiElements) {
        mapToOptional()
    }
    dataFixerComponent.addVariantsFromConfiguration(remappedRuntimeElements) {
        mapToOptional()
    }
    dataFixerComponent.addVariantsFromConfiguration(apiElements) {
        mapToOptional()
    }
    dataFixerComponent.addVariantsFromConfiguration(runtimeElements) {
        mapToOptional()
    }
    dataFixerComponent.addVariantsFromConfiguration(sourcesElements) {
        mapToOptional()
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
