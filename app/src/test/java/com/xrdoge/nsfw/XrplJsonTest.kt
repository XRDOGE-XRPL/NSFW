package com.xrdoge.nsfw

import com.xrdoge.nsfw.data.XrplJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XrplJsonTest {
    @Test
    fun parsesServerInfo() {
        val body = """
            {"result":{"info":{"server_state":"full","peers":42,"complete_ledgers":"32570-91000000",
            "validated_ledger":{"seq":91000000}},"status":"success"}}
        """.trimIndent()
        val status = XrplJson.parseNetworkStatus(body, "https://xrplcluster.com/")
        assertEquals("full", status.serverState)
        assertEquals(91000000L, status.ledgerIndex)
        assertEquals(42, status.peers)
    }

    @Test
    fun parsesAccountInfo() {
        val body = """
            {"result":{"account_data":{"Account":"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh",
            "Balance":"100000000","Sequence":7,"OwnerCount":2,"Flags":0},"status":"success"}}
        """.trimIndent()
        val account = XrplJson.parseAccountInfo(body)
        assertEquals("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh", account.address)
        assertEquals("100000000", account.balanceDrops)
        assertEquals(7L, account.sequence)
    }

    @Test
    fun parsesTrustLinesAndTx() {
        val linesBody = """
            {"result":{"lines":[{"account":"rIssuer","currency":"USD","balance":"10","limit":"1000"}],
            "status":"success"}}
        """.trimIndent()
        val lines = XrplJson.parseAccountLines(linesBody)
        assertEquals(1, lines.size)
        assertEquals("USD", lines[0].currency)

        val txBody = """
            {"result":{"transactions":[{"tx":{"hash":"ABCD","TransactionType":"Payment","Account":"rA",
            "Destination":"rB","Amount":"1000000"}}],"status":"success"}}
        """.trimIndent()
        val txs = XrplJson.parseAccountTx(txBody)
        assertEquals("Payment", txs[0].type)
        assertTrue(txs[0].amountLabel.contains("XRP"))
    }

    @Test
    fun parsesPrice() {
        val price = XrplJson.parseXrpPrice("""{"ripple":{"usd":2.5,"eur":2.3}}""")
        assertEquals(2.5, price.usd, 0.0)
        assertEquals(2.3, price.eur, 0.0)
    }
}
