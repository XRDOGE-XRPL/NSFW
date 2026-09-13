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
    }
}
