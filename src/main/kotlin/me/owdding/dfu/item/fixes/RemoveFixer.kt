package me.owdding.dfu.item.fixes

import me.owdding.dfu.item.DataComponentFixer
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Unit
import me.owdding.dfu.item.remove

class RemoveFixer(val key: String) : DataComponentFixer<Unit> {
    override val type: DataComponentType<Unit> = DataComponents.UNBREAKABLE // just a nonsense key, doesn't get used since we return null
    override fun getData(tag: CompoundTag): Unit? {
        tag.remove(key)
        return null
    }
}
