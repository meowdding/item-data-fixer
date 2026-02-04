import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.apply

plugins {
    idea
    kotlin("jvm")
    id("versioned-catalogues")
    id("com.google.devtools.ksp")
}

private val stonecutter = project.extensions.getByName("stonecutter") as dev.kikugie.stonecutter.build.StonecutterBuildExtension

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://maven.teamresourceful.com/repository/maven-public/", "earth.terrarium", "com.teamresourceful", "tech.thatgravyboat", "me.owdding")
    mavenCentral()
}

afterEvaluate {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
        compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    }

    tasks.named<Jar>("jar") {
        archiveClassifier = "${stonecutter.current.version}-dev"
    }

    tasks.named<Jar>("sourcesJar") {
        archiveClassifier = "${stonecutter.current.version}-sources"
    }

    tasks.named<ProcessResources>("processResources") {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to project.version,
                "minecraft" to versionedCatalog.versions["minecraft"]
            ))
        }
    }
}

(extensions.getByName("base") as BasePluginExtension).apply {
    archivesName = "meowdding-data-fixer"
}

extensions.getByType<IdeaModel>().apply {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}