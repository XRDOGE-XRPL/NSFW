package com.xrdoge.nsfw

import com.xrdoge.nsfw.util.XrplAddress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class XrplAddressTest {
    @Test
    fun acceptsClassicAddress() {
        assertTrue(XrplAddress.isClassicAddress("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
    }

    @Test
    fun rejectsSeedAndEmpty() {
        assertFalse(XrplAddress.isClassicAddress(""))
        assertFalse(XrplAddress.isClassicAddress("sEdV19BLfeQeKd"))
        assertFalse(XrplAddress.isClassicAddress("0xabc"))
        assertFalse(XrplAddress.isClassicAddress("r0illegal0OIl"))
    }

    @Test
    fun shortens() {
        assertEquals("rHb9CJ…tyTh", XrplAddress.shorten("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
    }
}
