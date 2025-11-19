package me.owdding.dfu.item

import com.google.common.collect.Multimap
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.block.entity.BannerPattern
import java.util.*

internal actual fun createPropertyMap(init: Multimap<String, Property>.() -> Unit): PropertyMap = PropertyMap().apply(init)
internal actual fun createResolvableProfile(name: String, uuid: UUID, properties: PropertyMap): ResolvableProfile = ResolvableProfile(Optional.ofNullable(name), Optional.ofNullable(uuid), properties)
internal actual fun CompoundTag.remove(s: String): Tag? {
    this.remove(s)
    return null
}
internal actual fun createBannerPattern(id: ResourceLocation, name: String): BannerPattern =
    BannerPattern(id, name)

internal actual typealias ResourceLocation = net.minecraft.resources.ResourceLocation
internal actual fun fromNamespaceAndPath(namespace: String, path: String): ResourceLocation =
    ResourceLocation.fromNamespaceAndPath(namespace, path)
internal actual fun withDefaultNamespace(string: String): ResourceLocation =
    ResourceLocation.withDefaultNamespace(string)
internal actual fun parse(string: String): ResourceLocation =
    ResourceLocation.parse(string)
internal actual fun tryParse(string: String): ResourceLocation? =
    ResourceLocation.tryParse(string)
internal actual fun ResourceLocation.toShortLanguageKey(): String =
    this.toShortLanguageKey()