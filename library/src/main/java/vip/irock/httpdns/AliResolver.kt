package vip.irock.httpdns

import org.json.JSONObject


class AliResolver : DnsResolver {

    override fun resolve(name: String): AddressEntry? {
        val account = DnsConfig.getAliyunAccount() ?: return null
        val resolveUrl = "$HTTP_SCHEMA$HOST/$account/d?host=$name"
        try {
            val response = HttpGet(resolveUrl).get()
            if (response.isNullOrEmpty())
                return null
            val json = JSONObject(response)
            val ttl = json.getLong("ttl")
            val ips = json.getJSONArray("ips")
            val res = ArrayList<String>()
            for (i in 0 until ips.length()) {
                val ip = ips.optString(i)
                if (InetAddressUtils.isNumericAddress(ip))
                    res.add(ip)
            }
            if (res.isEmpty())
                return null
            return AddressEntry(ttl * 1000L + System.currentTimeMillis(), res)
        } catch (e: Exception) {
            return null
        }
    }

    companion object {
        private const val HTTP_SCHEMA = "http://"
        const val HOST = "203.107.1.1"
    }
}