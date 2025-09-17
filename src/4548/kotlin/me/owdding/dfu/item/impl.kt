package me.owdding.dfu.item

import com.google.common.collect.LinkedHashMultimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

internal actual fun createPropertyMap(): PropertyMap = PropertyMap(LinkedHashMultimap.create())
internal actual fun createResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap): ResolvableProfile = ResolvableProfile.createResolved(GameProfile(uuid, name, properties))