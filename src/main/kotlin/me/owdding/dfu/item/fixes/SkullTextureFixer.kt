package me.owdding.dfu.item.fixes

import com.mojang.authlib.properties.Property
import me.owdding.dfu.item.DataComponentFixer
import me.owdding.dfu.item.utils.createResolvableProfile
import me.owdding.dfu.item.utils.createPropertyMap
import me.owdding.dfu.item.utils.getStringOrNull
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.component.ResolvableProfile
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

object SkullTextureFixer : DataComponentFixer<ResolvableProfile> {

    private val cache = ConcurrentHashMap<String, ResolvableProfile>()
    private const val TAG = "SkullOwner"

    override val type: DataComponentType<ResolvableProfile> = DataComponents.PROFILE

    override fun getData(tag: CompoundTag): ResolvableProfile? {
        val skullOwner = tag.getAndRemoveCompound(TAG) ?: return null

        val properties = skullOwner.getCompound("Properties").getOrNull() ?: return null
        val textures = properties.getList("textures").getOrNull() ?: return null
        val texture = textures.first().asCompound().getOrNull()?.getStringOrNull("Value") ?: return null

        return cache.getOrPut(texture) {
            createResolvableProfile(
                "meow", UUID.randomUUID(),
                createPropertyMap {
                    put("textures", Property("textures", texture))
                },
            )
        }
    }
}
