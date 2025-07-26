package me.owdding.dfu.item

import me.owdding.dfu.item.base.BaseItem
import me.owdding.dfu.item.fixes.*
import me.owdding.dfu.item.fixes.display.ColorFixer
import me.owdding.dfu.item.fixes.display.LoreFixer
import me.owdding.dfu.item.fixes.display.NameFixer
import me.owdding.dfu.item.utils.getStringOrNull
import me.owdding.dfu.item.utils.holder
import me.owdding.dfu.item.utils.toJson
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack

object LegacyDataFixer {

    private val fixers = listOf(
        HideFlagsFixer,
        SkullTextureFixer,
        LoreFixer,
        NameFixer,
        ColorFixer,
        UnbreakableFixer,
        EnchantGlintFixer,
        WrittenBookFixer,
        BannerItemFixer,
        ExtraAttributesFixer,
        FireworkExplosionFixer,
        ItemModelFix,
        RemoveFixer("overrideMeta"),
        RemoveFixer("AttributeModifiers"),
    )

    fun fromTag(tag: Tag): ItemStack? {
        if (tag !is CompoundTag) {
            return ItemStack.EMPTY
        }

        if (tag.isEmpty) return ItemStack.EMPTY

        val base = BaseItem.getBase(tag)

        if (base == null) {
            MeowddingItemDfu.error(
                "Base item not found for ${tag.getStringOrNull("id")} (${tag.getStringOrNull("Damage")})\n${
                    NbtUtils.prettyPrint(
                        tag
                    )
                }"
            )
            return null
        }

        val (item, count, builder) = base

        tag.getCompound("tag").ifPresent { tag ->
            fixers.forEach {
                if (!it.canApply(item)) return@forEach
                it.apply(builder, tag)
            }
        }

        val stack = ItemStack(item.holder, count, builder.build())

        if (MeowddingItemDfu.logErrors && !tag.isEmpty && !tag.getCompoundOrEmpty("tag").isEmpty) {
            MeowddingItemDfu.warn(
                """
            Item tag is not empty after applying fixers for ${stack.get(DataComponents.CUSTOM_DATA)?.unsafe?.getString("id")}:
            ${NbtUtils.prettyPrint(tag)}
            ${stack.toJson(ItemStack.CODEC)}
            """.trimIndent(),
            )
        }

        return stack
    }
}
