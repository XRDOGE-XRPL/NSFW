package com.xrdoge.nsfw.util

object XrplAddress {
    private val CLASSIC = Regex("^r[1-9A-HJ-NP-Za-km-z]{24,34}$")

    fun isClassicAddress(value: String): Boolean {
        val trimmed = value.trim()
        return CLASSIC.matches(trimmed)
    }

    fun normalize(value: String): String = value.trim()

    fun shorten(value: String, head: Int = 6, tail: Int = 4): String {
        val address = normalize(value)
        if (address.length <= head + tail + 1) return address
        return "${address.take(head)}…${address.takeLast(tail)}"
    }
}
