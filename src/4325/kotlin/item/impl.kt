package me.owdding.dfu.item

import com.google.common.collect.Multimaps
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.Optional
import java.util.UUID

internal actual fun createPropertyMap(): PropertyMap = PropertyMap()
internal actual fun createResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap): ResolvableProfile = ResolvableProfile(Optional.ofNullable(name), Optional.ofNullable(uuid), properties)