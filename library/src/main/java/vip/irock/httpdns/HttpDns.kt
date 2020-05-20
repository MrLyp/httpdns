package vip.irock.httpdns

import android.util.Log

class HttpDns {

    companion object {
        private var sDnsCache = DnsCache(CachePolicy.EXPIRED)
    }

    private val mDnsResolver = AliResolver()

    private val mSpeeder: Speeder by lazy { Speeder() }

    fun getAddressByName(name: String): String? {
        return getAllAddressByName(name)?.get(0)
    }

    fun getAllAddressByName(name: String): List<String>? {
        val entry = sDnsCache.get(name)
        if (entry != null) {
            Log.d("dns", "load from cache")
            val list = entry.ips
            if (entry.expiration < System.currentTimeMillis()) {
                checkUpdate(name)
            }
            return list
        }
        val resolveResult = mDnsResolver.resolve(name) ?: return null
        mSpeeder.sortAsync(sDnsCache, name, resolveResult)
        return resolveResult.ips
    }

    fun setAllowExpired(allow: Boolean) {
        if (allow && sDnsCache.getPolicy() != CachePolicy.FOREVER)
            sDnsCache = DnsCache(CachePolicy.FOREVER)
        if (!allow && sDnsCache.getPolicy() != CachePolicy.EXPIRED)
            sDnsCache = DnsCache(CachePolicy.EXPIRED)
    }

    private fun checkUpdate(host: String) {
        DnsConfig.getExecutors().execute {
            val resolveResult = mDnsResolver.resolve(host) ?: return@execute
            mSpeeder.sortAsync(sDnsCache, host, resolveResult)
        }
    }
}