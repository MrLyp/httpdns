package vip.irock.httpdns


class TencentResolver : DnsResolver {

    override fun resolve(name: String): AddressEntry? {
        val resolveUrl = "$HTTP_SCHEMA$HOST/d?dn=$name.&ttl=1"
        try {
            val response = HttpGet(resolveUrl).get() ?: return null
            return parse(response)
        } catch (e: Exception) {
            return null
        }
    }

    private fun parse(response: String): AddressEntry? {
        val array = response.split(",")
        if (array.size != 2)
            return null
        val ips = array[0].split(";")
        if (ips.isEmpty())
            return null
        val ttl = array[1].toInt()
        val expired = System.currentTimeMillis() + ttl * 1000L
        return AddressEntry(expired, ips.filter {
            InetAddressUtils.isNumericAddress(it)
        })
    }

    companion object {
        private const val HTTP_SCHEMA = "http://"
        const val HOST = "119.29.29.29"
    }
}