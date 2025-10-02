package me.owdding.dfu.item.fixes

import me.owdding.dfu.item.DataComponentFixer
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.getOrPut

object ItemModelFix : DataComponentFixer<ResourceLocation> {

    private val cache = ConcurrentHashMap<String, ResourceLocation>()
    override val type: DataComponentType<ResourceLocation> = DataComponents.ITEM_MODEL

    override fun getData(tag: CompoundTag) = tag.getAndRemoveString("ItemModel")?.let {
        cache.getOrPut(it) {
            ResourceLocation.tryParse(it)
        }
    }

}
