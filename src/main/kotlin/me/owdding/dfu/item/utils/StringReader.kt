package me.owdding.dfu.item.utils

import kotlin.math.max

internal data class StringReader(val text: String) {
    var cursor: Int = 0
    val maxIndex: Int = text.length - 1

    fun canRead() = cursor <= maxIndex
    fun peek() = if (canRead()) text[cursor] else null
    fun previous() = text[max(cursor - 1, 0)]
    fun read() = text[cursor++]
    fun skip() {
        cursor++
    }

    fun readUntil(terminating: Char): String = with(StringBuilder()) {
        while (canRead() && peek() != terminating) {
            append(read())
        }
        skip()
        this
    }.toString()
}
