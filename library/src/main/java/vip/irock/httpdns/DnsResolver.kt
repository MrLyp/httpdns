package vip.irock.httpdns

interface DnsResolver {

    fun resolve(name: String): AddressEntry?
}