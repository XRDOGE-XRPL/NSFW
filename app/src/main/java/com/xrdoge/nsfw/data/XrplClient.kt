package com.xrdoge.nsfw.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class XrplClient(
    private val http: OkHttpClient = defaultClient(),
) {
    suspend fun serverInfo(rpcUrl: String): NetworkStatus = withContext(Dispatchers.IO) {
        val body = post(rpcUrl, XrplJson.rpcBody("server_info"))
        XrplJson.parseNetworkStatus(body, rpcUrl)
    }

    suspend fun accountInfo(rpcUrl: String, account: String): AccountSnapshot =
        withContext(Dispatchers.IO) {
            val params = JSONObject()
                .put("account", account)
                .put("ledger_index", "validated")
                .put("strict", true)
            val body = post(rpcUrl, XrplJson.rpcBody("account_info", params))
            XrplJson.parseAccountInfo(body)
        }

    suspend fun accountLines(rpcUrl: String, account: String): List<TrustLine> =
        withContext(Dispatchers.IO) {
            val params = JSONObject()
                .put("account", account)
                .put("ledger_index", "validated")
            val body = post(rpcUrl, XrplJson.rpcBody("account_lines", params))
            XrplJson.parseAccountLines(body)
        }

    suspend fun accountTx(rpcUrl: String, account: String, limit: Int = 15): List<LedgerTx> =
        withContext(Dispatchers.IO) {
            val params = JSONObject()
                .put("account", account)
                .put("ledger_index_min", -1)
                .put("ledger_index_max", -1)
                .put("limit", limit)
                .put("binary", false)
            val body = post(rpcUrl, XrplJson.rpcBody("account_tx", params))
            XrplJson.parseAccountTx(body)
        }

    suspend fun xrpPrice(): XrpPrice = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/simple/price?ids=ripple&vs_currencies=usd,eur")
            .header("Accept", "application/json")
            .build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Preis-API ${response.code}")
            }
            XrplJson.parseXrpPrice(response.body?.string().orEmpty())
        }
    }

    private fun post(rpcUrl: String, json: String): String {
        val request = Request.Builder()
            .url(rpcUrl)
            .header("Content-Type", "application/json")
            .post(json.toRequestBody(JSON))
            .build()
        http.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IllegalStateException("RPC ${response.code}: ${body.take(180)}")
            }
            return body
        }
    }

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaType()

        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }
}
