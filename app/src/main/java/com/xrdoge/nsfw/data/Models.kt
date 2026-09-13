package com.xrdoge.nsfw.data

data class NetworkStatus(
    val serverState: String,
    val ledgerIndex: Long,
    val peers: Int,
    val completeLedgers: String,
    val rpcUrl: String,
)

data class XrpPrice(
    val usd: Double,
    val eur: Double,
)

data class AccountSnapshot(
    val address: String,
    val balanceDrops: String,
    val sequence: Long,
    val ownerCount: Int,
    val flags: Long,
)

data class TrustLine(
    val issuer: String,
    val currency: String,
    val balance: String,
    val limit: String,
)

data class LedgerTx(
    val hash: String,
    val type: String,
    val account: String,
    val destination: String?,
    val amountLabel: String,
    val date: String?,
)

data class AppSettings(
    val rpcUrl: String = DEFAULT_RPC,
    val savedAccount: String = "",
    val tokenCurrency: String = "NSFW",
    val tokenIssuer: String = "",
) {
    companion object {
        const val DEFAULT_RPC = "https://xrplcluster.com/"
        val RPC_OPTIONS = listOf(
            "https://xrplcluster.com/",
            "https://s1.ripple.com:51234/",
            "https://s2.ripple.com:51234/",
        )
    }
}
