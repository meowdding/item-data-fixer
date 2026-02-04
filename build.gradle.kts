@file:Suppress("UnstableApiUsage")

plugins {
    `item-dfu`
    id("net.fabricmc.fabric-loom")
}

dependencies {
    minecraft(versionedCatalog["minecraft"])
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