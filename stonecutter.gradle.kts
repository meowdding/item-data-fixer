plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
}
stonecutter active "4663"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    replacements.string("identifier") {
        direction = eval(current.version, "<4663")
        replace("import net.minecraft.resources.Identifier", "import net.minecraft.resources.ResourceLocation as Identifier")
    }
}