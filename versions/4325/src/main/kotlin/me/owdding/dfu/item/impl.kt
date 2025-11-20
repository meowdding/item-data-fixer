package me.owdding.dfu.item

import com.google.common.collect.Multimap
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile

import java.util.*

internal fun createPropertyMap(init: Multimap<String, Property>.() -> Unit): PropertyMap = PropertyMap().apply(init)
internal fun createResolvableProfile(name: String, uuid: UUID, properties: PropertyMap): ResolvableProfile =
    ResolvableProfile(Optional.ofNullable(name), Optional.ofNullable(uuid), properties)

