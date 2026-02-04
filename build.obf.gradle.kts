@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask

plugins {
    `item-dfu`
    id("fabric-loom")
}

dependencies {
    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })
}

val mcVersion = stonecutter.current.version.replace(".", "")
loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=" + '"' + rootProject.projectDir.resolve("run/${mcVersion}Mods").absolutePath + '"')
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

tasks.named<RemapJarTask>("remapJar") {
    archiveBaseName = "item-data-fixer"
    archiveClassifier = stonecutter.current.version
}

tasks.named("remapSourcesJar", RemapSourcesJarTask::class) {
    archiveBaseName = "item-data-fixer"
    archiveClassifier = stonecutter.current.version + "-sources"
}