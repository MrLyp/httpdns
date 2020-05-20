package vip.irock.httpdns

import androidx.collection.LruCache
import java.util.*

enum class CachePolicy {
    FOREVER, EXPIRED
}

class DnsCache(private val policy: CachePolicy) {

    private val cache = LruCache<String, AddressEntry>(MAX_SIZE)

    @Synchronized
    fun get(name: String): AddressEntry? {
        val entry = cache.get(name) ?: return null
        if (policy != CachePolicy.FOREVER && entry.expiration < System.currentTimeMillis()) {
            cache.remove(name)
            return null
        }
        return entry
    }

    @Synchronized
    fun put(host: String, address: AddressEntry) {
        clearExpiredIfPossible()
        cache.put(host, address)
    }

    fun getPolicy(): CachePolicy {
        return policy
    }

    private fun clearExpiredIfPossible() {
        if (policy == CachePolicy.FOREVER)
            return
        val snapShot = cache.snapshot()
        val now = System.currentTimeMillis()
        val expired = LinkedList<String>()
        for (key in snapShot.keys) {
            val entry = snapShot[key] ?: continue
            if (entry.expiration < now)
                expired.add(key)
        }
        for (key in expired)
            cache.remove(key)
    }

    companion object {
        private const val MAX_SIZE: Int = 16
    }
}