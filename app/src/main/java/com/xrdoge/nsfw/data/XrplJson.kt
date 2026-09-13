package com.xrdoge.nsfw.data

import org.json.JSONArray
import org.json.JSONObject

object XrplJson {
    fun requireResult(body: String): JSONObject {
        val root = JSONObject(body)
        val result = root.optJSONObject("result") ?: throw IllegalStateException("Keine XRPL-Antwort")
        val status = result.optString("status")
        if (status == "error") {
            val error = result.optString("error_message", result.optString("error", "XRPL-Fehler"))
            throw IllegalStateException(error)
        }
        return result
    }

    fun parseNetworkStatus(body: String, rpcUrl: String): NetworkStatus {
        val info = requireResult(body).optJSONObject("info") ?: JSONObject()
        val ledger = info.optJSONObject("validated_ledger") ?: JSONObject()
        return NetworkStatus(
            serverState = info.optString("server_state", "unknown"),
            ledgerIndex = ledger.optLong("seq", info.optLong("validated_ledger", 0)),
            peers = info.optInt("peers", 0),
            completeLedgers = info.optString("complete_ledgers", ""),
            rpcUrl = rpcUrl,
        )
    }

    fun parseAccountInfo(body: String): AccountSnapshot {
        val data = requireResult(body).optJSONObject("account_data")
            ?: throw IllegalStateException("Konto nicht gefunden")
        return AccountSnapshot(
            address = data.optString("Account"),
            balanceDrops = data.optString("Balance", "0"),
            sequence = data.optLong("Sequence", 0),
            ownerCount = data.optInt("OwnerCount", 0),
            flags = data.optLong("Flags", 0),
        )
    }

    fun parseAccountLines(body: String): List<TrustLine> {
        val lines = requireResult(body).optJSONArray("lines") ?: JSONArray()
        return buildList {
            for (i in 0 until lines.length()) {
                val line = lines.optJSONObject(i) ?: continue
                add(
                    TrustLine(
                        issuer = line.optString("account"),
                        currency = line.optString("currency"),
                        balance = line.optString("balance"),
                        limit = line.optString("limit"),
                    ),
                )
            }
        }
    }

    fun parseAccountTx(body: String): List<LedgerTx> {
        val txs = requireResult(body).optJSONArray("transactions") ?: JSONArray()
        return buildList {
            for (i in 0 until txs.length()) {
                val wrapper = txs.optJSONObject(i) ?: continue
                val tx = wrapper.optJSONObject("tx")
                    ?: wrapper.optJSONObject("tx_json")
                    ?: continue
                val hash = tx.optString("hash", wrapper.optString("hash"))
                add(
                    LedgerTx(
                        hash = hash,
                        type = tx.optString("TransactionType", "Unknown"),
                        account = tx.optString("Account"),
                        destination = tx.optString("Destination").ifBlank { null },
                        amountLabel = formatAmount(tx.opt("Amount") ?: tx.opt("DeliverMax")),
                        date = wrapper.opt("date")?.toString(),
                    ),
                )
            }
        }
    }

    fun parseXrpPrice(body: String): XrpPrice {
        val ripple = JSONObject(body).optJSONObject("ripple") ?: JSONObject()
        return XrpPrice(
            usd = ripple.optDouble("usd", 0.0),
            eur = ripple.optDouble("eur", 0.0),
        )
    }

    fun rpcBody(method: String, params: JSONObject = JSONObject()): String {
        return JSONObject()
            .put("method", method)
            .put("params", JSONArray().put(params))
            .toString()
    }

    private fun formatAmount(raw: Any?): String {
        return when (raw) {
            null, JSONObject.NULL -> "—"
            is JSONObject -> {
                val value = raw.optString("value", "0")
                val currency = raw.optString("currency", "")
                "$value $currency"
            }
            else -> {
                val drops = raw.toString()
                if (drops.all { it.isDigit() }) {
                    val xrp = drops.toBigDecimalOrNull()?.movePointLeft(6)?.stripTrailingZeros()?.toPlainString()
                    "${xrp ?: drops} XRP"
                } else {
                    drops
                }
            }
        }
    }
}
