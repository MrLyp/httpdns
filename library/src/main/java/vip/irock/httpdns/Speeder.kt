package vip.irock.httpdns

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import kotlin.collections.HashMap

class Speeder {

    fun sortAsync(cache: DnsCache, name: String, address: AddressEntry) {
        val executor = DnsConfig.getExecutors()
        executor.execute(SortTask(cache, name, address))
    }
}

class SortTask(
    private val cache: DnsCache,
    private val name: String,
    private val address: AddressEntry
) : Runnable {
    override fun run() {
        val executor = DnsConfig.getExecutors()
        val ipList = address.ips
        val length = ipList.size
        val taskMap = HashMap<String, Future<PingStats>>(length)
        val countDownLatch = CountDownLatch(length)
        for (ip in ipList) {
            if (taskMap.contains(ip).not()) {
                val future: Future<PingStats> = executor.submit(SpeedTask(ip, countDownLatch))
                taskMap[ip] = future
            }
        }
        countDownLatch.await()
        val entryMap = HashMap<String, PingStats>(length)
        for (key in taskMap.keys) {
            val value = taskMap[key]
            value?.get()?.let {
                entryMap[key] = it
            }
        }
        if (entryMap.size != ipList.size)
            return
        Collections.sort(address.ips, object : Comparator<String> {
            override fun compare(o1: String, o2: String): Int {
                val s1 = entryMap[o1] ?: return o1.compareTo(o2)
                val s2 = entryMap[o2] ?: return o1.compareTo(o2)
                if ((s1.averageTime > 0) and (s2.averageTime > 0))
                    return s1.averageTime.compareTo(s2.averageTime)
                else if (s1.averageTime > 0)
                    return -1
                else if (s2.averageTime > 0)
                    return 1
                else
                    return o1.compareTo(o2)
            }
        })
        taskMap.clear()
        cache.put(name, address)
    }
}

class SpeedTask(private val ip: String, private val latch: CountDownLatch) : Callable<PingStats> {

    override fun call(): PingStats {
        val stats = Ping(ip, 5, 1000).ping()
        latch.countDown()
        return stats
    }

}