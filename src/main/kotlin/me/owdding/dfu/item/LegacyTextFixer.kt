package me.owdding.dfu.item

import me.owdding.dfu.item.utils.StringReader
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object LegacyTextFixer {

    val EMPTY: Style = Style.EMPTY.withItalic(false)

    private const val CONTROL_CHAR = '§'
    val codeMap = buildMap {
        fun put(formatting: ChatFormatting, init: Style.() -> Style) {
            put(formatting.char.lowercaseChar(), init)
        }

        ChatFormatting.entries.filter { it.isColor }.forEach { formatting -> put(formatting) { EMPTY.withColor(formatting) } }

        put(ChatFormatting.BOLD) { this.withBold(true) }
        put(ChatFormatting.ITALIC) { this.withItalic(true) }
        put(ChatFormatting.STRIKETHROUGH) { this.withStrikethrough(true) }
        put(ChatFormatting.UNDERLINE) { this.withUnderlined(true) }
        put(ChatFormatting.OBFUSCATED) { this.withObfuscated(true) }

        put(ChatFormatting.RESET) { EMPTY }
    }

    fun parse(text: String): Component = Component.empty().apply {
        if (!text.contains(CONTROL_CHAR)) {
            append(text)
            return@apply
        }

        var last: Style = EMPTY
        with(StringReader(text)) {
            append(this.readUntil(CONTROL_CHAR))

            while (this.canRead()) {
                codeMap[this.read().lowercaseChar()]?.let { last = last.it() } ?: MeowddingItemDfu.warn("Unknown control character ${this.peek()} in text $text")

                if (peek() == CONTROL_CHAR) {
                    skip()
                    continue
                }

                val readStringUntil = readUntil(CONTROL_CHAR)
                append(Component.literal(readStringUntil).withStyle(last))

                last = EMPTY
            }
        }
    }

}
