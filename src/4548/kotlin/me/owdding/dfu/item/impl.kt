package me.owdding.dfu.item

import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

internal actual fun createPropertyMap(init: Multimap<String, Property>.() -> Unit): PropertyMap = PropertyMap(LinkedHashMultimap.create<String, Property>().apply(init))
internal actual fun createResolvableProfile(name: String, uuid: UUID, properties: PropertyMap): ResolvableProfile = ResolvableProfile.createResolved(GameProfile(uuid, name, properties))