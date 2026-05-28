package me.owdding.dfu.item

import me.owdding.dfu.item.utils.StringReader
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object LegacyTextFixer {

    val EMPTY: Style = Style.EMPTY.withItalic(false)

    private const val CONTROL_CHAR = '§'
    val codeMap = buildMap<Char, Style.() -> Style> {
        fun put(char: Char, init: Style.() -> Style) {
            put(char.lowercaseChar(), init)
        }

        // Color Codes
        put('0') { EMPTY.withColor(ChatFormatting.BLACK) }
        put('1') { EMPTY.withColor(ChatFormatting.DARK_BLUE) }
        put('2') { EMPTY.withColor(ChatFormatting.DARK_GREEN) }
        put('3') { EMPTY.withColor(ChatFormatting.DARK_AQUA) }
        put('4') { EMPTY.withColor(ChatFormatting.DARK_RED) }
        put('5') { EMPTY.withColor(ChatFormatting.DARK_PURPLE) }
        put('6') { EMPTY.withColor(ChatFormatting.GOLD) }
        put('7') { EMPTY.withColor(ChatFormatting.GRAY) }
        put('8') { EMPTY.withColor(ChatFormatting.DARK_GRAY) }
        put('9') { EMPTY.withColor(ChatFormatting.BLUE) }
        put('a') { EMPTY.withColor(ChatFormatting.GREEN) }
        put('b') { EMPTY.withColor(ChatFormatting.AQUA) }
        put('c') { EMPTY.withColor(ChatFormatting.RED) }
        put('d') { EMPTY.withColor(ChatFormatting.LIGHT_PURPLE) }
        put('e') { EMPTY.withColor(ChatFormatting.YELLOW) }
        put('f') { EMPTY.withColor(ChatFormatting.WHITE) }

        // Format Modifiers
        put('k') { this.withObfuscated(true) }
        put('l') { this.withBold(true) }
        put('m') { this.withStrikethrough(true) }
        put('n') { this.withUnderlined(true) }
        put('o') { this.withItalic(true) }
        put('r') { EMPTY }
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
