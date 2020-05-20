package vip.irock.httpdns

class AutoResolver : DnsResolver {

    private var mRealResolver: DnsResolver by LazyMutable { loadCacheResolver() }

    override fun resolve(name: String): AddressEntry? {
        return mRealResolver.resolve(name)
    }

    private fun loadCacheResolver(): DnsResolver {
        return TencentResolver()
    }

    private fun autoChoose() {

    }
}