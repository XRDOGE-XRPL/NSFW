package com.xrdoge.nsfw.util

object CurrencyCodec {
    fun display(code: String): String {
        val trimmed = code.trim()
        if (trimmed.length <= 3) return trimmed
        if (trimmed.length == 40 && trimmed.all { it in "0123456789ABCDEFabcdef" }) {
            val bytes = trimmed.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val ascii = bytes.takeWhile { it != 0.toByte() }
                .map { it.toInt().toChar() }
                .joinToString("")
            if (ascii.isNotEmpty() && ascii.all { it.isLetterOrDigit() || it == '_' || it == '-' }) {
                return ascii
            }
        }
        return trimmed
    }

    fun toXrplCode(human: String): String {
        val trimmed = human.trim()
        if (trimmed.length <= 3) return trimmed.uppercase()
        val hex = trimmed.toByteArray(Charsets.US_ASCII)
            .joinToString("") { "%02X".format(it) }
            .padEnd(40, '0')
        return hex.take(40)
    }
}
