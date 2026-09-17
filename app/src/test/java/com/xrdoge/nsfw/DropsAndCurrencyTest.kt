package com.xrdoge.nsfw

import com.xrdoge.nsfw.util.CurrencyCodec
import com.xrdoge.nsfw.util.Drops
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class DropsAndCurrencyTest {
    @Test
    fun convertsDropsToXrp() {
        assertEquals(BigDecimal("12.5"), Drops.toXrp("12500000"))
        assertEquals("1", Drops.formatXrp("1000000"))
    }

    @Test
    fun encodesAndDecodesNsfwCurrency() {
        val hex = CurrencyCodec.toXrplCode("NSFW")
        assertEquals(40, hex.length)
        assertEquals("NSFW", CurrencyCodec.display(hex))
        assertEquals("USD", CurrencyCodec.display("USD"))
        assertEquals("USD", CurrencyCodec.display("usd"))
    }

    @Test
    fun formatsFiatWithNormalizedCurrencyCodes() {
        val eur = Drops.formatFiat(1.23, "eur")
        val usd = Drops.formatFiat(1.23, "usd")
        assertEquals("1,23 €", eur.replace("\u00A0", " "))
        assertEquals("$1.23", usd.replace("\u00A0", " "))
    }
}
