package me.owdding.dfu.item

import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.block.entity.BannerPattern
import java.util.*

internal actual fun createPropertyMap(init: Multimap<String, Property>.() -> Unit): PropertyMap = PropertyMap(LinkedHashMultimap.create<String, Property>().apply(init))
internal actual fun createResolvableProfile(name: String, uuid: UUID, properties: PropertyMap): ResolvableProfile = ResolvableProfile.createResolved(GameProfile(uuid, name, properties))
internal actual fun CompoundTag.remove(s: String) = this.remove(s)
internal actual fun createBannerPattern(id: ResourceLocation, name: String): BannerPattern =
    BannerPattern(id, name)

internal actual typealias ResourceLocation = Identifier
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