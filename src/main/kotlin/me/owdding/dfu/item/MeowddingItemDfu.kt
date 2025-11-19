package me.owdding.dfu.item

import com.google.common.collect.Multimap
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.block.entity.BannerPattern
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

internal expect fun createPropertyMap(init: Multimap<String, Property>.() -> Unit): PropertyMap
internal expect fun createResolvableProfile(name: String, uuid: UUID, properties: PropertyMap): ResolvableProfile
internal expect fun CompoundTag.remove(s: String): Tag?
internal expect fun createBannerPattern(id: ResourceLocation, name: String): BannerPattern

expect class ResourceLocation
internal expect fun fromNamespaceAndPath(namespace: String, path: String): ResourceLocation
internal expect fun withDefaultNamespace(string: String): ResourceLocation
internal expect fun parse(string: String): ResourceLocation
internal expect fun tryParse(string: String): ResourceLocation?
internal expect fun ResourceLocation.toShortLanguageKey(): String



object MeowddingItemDfu : Logger by LoggerFactory.getLogger("MeowddingItemDfu") {

    var logErrors: Boolean = false

    fun load() = LegacyDataFixer
    internal fun id(path: String): ResourceLocation = fromNamespaceAndPath("meowdding", path)

}