package me.owdding.dfu.item.utils

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import net.minecraft.core.Holder
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import kotlin.jvm.optionals.getOrNull

internal fun CompoundTag.getStringOrNull(key: String): String? = this.getString(key).getOrNull()

internal val Item.holder: Holder<Item> get() = this.builtInRegistryHolder()

internal val ops: DynamicOps<JsonElement> get() {
    return VanillaRegistries.createLookup().createSerializationContext(JsonOps.INSTANCE)
}

internal fun <T : Any> T.toJson(codec: Codec<T>): JsonElement? {
    return codec.encodeStart(ops, this).result().getOrNull()
}