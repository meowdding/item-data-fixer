plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

fun plugin(provider: Provider<PluginDependency>): Provider<String> = provider.map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

dependencies {
    implementation(libs.gson)
    implementation(plugin(libs.plugins.ksp))
    implementation(plugin(libs.plugins.stonecutter))
    implementation(plugin(libs.plugins.kotlin))
}