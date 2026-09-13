package com.xrdoge.nsfw.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object Drops {
    private val DROPS_PER_XRP = BigDecimal("1000000")

    fun toXrp(drops: String): BigDecimal {
        val raw = drops.trim()
        if (raw.isEmpty()) return BigDecimal.ZERO
        return BigDecimal(raw).divide(DROPS_PER_XRP)
    }

    fun formatXrp(drops: String, decimals: Int = 6): String {
        val xrp = toXrp(drops).setScale(decimals, RoundingMode.DOWN).stripTrailingZeros()
        return xrp.toPlainString()
    }

    fun formatFiat(amount: Double, currency: String = "USD"): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        if (currency == "EUR") {
            return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(amount)
        }
        return format.format(amount)
    }
}
