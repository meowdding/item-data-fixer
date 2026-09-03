package me.owdding.dfu.item

import me.owdding.dfu.item.base.BaseItem
import me.owdding.dfu.item.fixes.*
import me.owdding.dfu.item.fixes.display.ColorFixer
import me.owdding.dfu.item.fixes.display.LoreFixer
import me.owdding.dfu.item.fixes.display.NameFixer
import me.owdding.dfu.item.utils.getStringOrNull
import me.owdding.dfu.item.utils.holder
import me.owdding.dfu.item.utils.toJson
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.*
import net.minecraft.resources.Identifier
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
                    prettyPrint(tag)
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

        val hasEncounteredError = mutableSetOf<Identifier>()

        tag.getCompound("components").ifPresent { tag ->
            tag.keySet().mapNotNull { Identifier.tryParse(it)?.let { id -> it to id } }.forEach { (key, identifier) ->
                BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(identifier).ifPresent { componentType ->
                    val value = tag.get(key) ?: return@ifPresent

                    if (builder.set(componentType, value)) {
                        tag.remove(key)
                    } else {
                        hasEncounteredError.add(identifier)
                    }
                }
            }
        }

        val stack = ItemStack(item.holder, count, builder.build())

        if (hasEncounteredError.isNotEmpty()) {
            MeowddingItemDfu.warn(
                """
            Failed to decode one or more components (${hasEncounteredError.joinToString(", ")}) in ${
                    stack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getString("id")
                }:
            ${prettyPrint(tag)}
            ${stack.toJson(ItemStack.CODEC)}
            """.trimIndent(),
            )
        }

        if (MeowddingItemDfu.logErrors && !tag.isEmpty && !tag.getCompoundOrEmpty("tag").isEmpty && hasEncounteredError.isEmpty()) {
            MeowddingItemDfu.warn(
                """
            Item tag is not empty after applying fixers for ${
                    stack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getString("id")
                }:
            ${prettyPrint(tag)}
            ${stack.toJson(ItemStack.CODEC)}
            """.trimIndent(),
            )
        }

        return stack
    }

    fun <Type : Any> DataComponentPatch.Builder.set(dataType: DataComponentType<Type>, tag: Tag): Boolean =
        dataType.codec()?.parse(NbtOps.INSTANCE, tag)?.ifSuccess { type ->
            set(dataType, type)
        }?.isSuccess == true

    private fun prettyPrint(tag: Tag): String = buildString {
        prettyPrint(tag, 0, true)
    }

    fun StringBuilder.prettyPrint(input: Tag, indent: Int, withBinaryBlobs: Boolean): StringBuilder {
        when (input) {
            is PrimitiveTag -> append(input)
            is EndTag -> {}
            is ByteArrayTag -> {
                val array = input.asByteArray
                printBlob("byte", array.size, indent, withBinaryBlobs) {
                    array[it].toUByte().toString(16).padStart(2, '0').uppercase()
                }
            }

            is IntArrayTag -> {
                val array = input.asIntArray
                val maxLen = array.maxOfOrNull { it.toUInt().toString(16).length } ?: 0
                printBlob("int", array.size, indent, withBinaryBlobs) {
                    array[it].toUInt().toString(16).padStart(maxLen, '0').uppercase()
                }
            }

            is LongArrayTag -> {
                val array = input.asLongArray
                val maxLen = array.maxOfOrNull { it.toULong().toString(16).length } ?: 0
                printBlob("long", array.size, indent, withBinaryBlobs) {
                    array[it].toULong().toString(16).padStart(maxLen, '0').uppercase()
                }
            }

            is ListTag -> {
                appendIndent(indent).append("list[").append(input.size).append("] [")

                if (input.size > 0) append('\n')

                for (i in 0 until input.size) {
                    if (i > 0) append(",\n")
                    appendIndent(indent + 1)
                    prettyPrint(input.get(i), indent + 1, withBinaryBlobs)
                }

                if (input.size > 0) append('\n')

                appendIndent(indent).append(']')
            }

            is CompoundTag -> {
                val keys = input.keySet().sorted()
                appendIndent(indent).append('{')

                if (length - lastIndexOf("\n") > 2 * (indent + 1)) {
                    append('\n')
                    appendIndent(indent + 1)
                }

                val maxLen = keys.maxOfOrNull { it.length } ?: 0

                keys.forEachIndexed { i, key ->
                    if (i > 0) append(",\n")

                    appendIndent(indent + 1)
                        .append('"').append(key).append('"')
                        .append(" ".repeat(maxLen - key.length)).append(": ")

                    prettyPrint(input.get(key) ?: return@forEachIndexed, indent + 1, withBinaryBlobs)
                }

                if (keys.isNotEmpty()) append('\n')

                appendIndent(indent).append('}')
            }

            else -> throw IllegalArgumentException("Unsupported tag type: $input")
        }
        return this
    }

    private inline fun StringBuilder.printBlob(
        type: String,
        size: Int,
        indent: Int,
        withBinaryBlobs: Boolean,
        crossinline getHex: (Int) -> String
    ) {
        appendIndent(indent).append(type).append('[').append(size).append("] {\n")

        if (withBinaryBlobs) {
            appendIndent(indent + 1)
            for (i in 0 until size) {
                if (i > 0) {
                    append(',')
                    if (i % 16 == 0) {
                        append('\n')
                        appendIndent(indent + 1)
                    } else {
                        append(' ')
                    }
                }
                append("0x").append(getHex(i))
            }
        } else {
            appendIndent(indent + 1).append(" // Skipped, supply withBinaryBlobs true")
        }

        append('\n')
        appendIndent(indent).append('}')
    }

    private fun StringBuilder.appendIndent(indent: Int): StringBuilder {
        val len = length - lastIndexOf("\n") - 1
        val target = 2 * indent
        if (len < target) {
            append(" ".repeat(target - len))
        }
        return this
    }
}
