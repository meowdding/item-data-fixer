package me.owdding.dfu.item

import com.mojang.authlib.properties.PropertyMap
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.ResolvableProfile
import net.msrandom.stub.Stub
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

@Stub
internal expect fun createPropertyMap(): PropertyMap
@Stub
internal expect fun createResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap): ResolvableProfile

object MeowddingItemDfu : Logger by LoggerFactory.getLogger("MeowddingItemDfu") {

    var logErrors: Boolean = false

    fun load() = LegacyDataFixer
    internal fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("meowdding", path)

}