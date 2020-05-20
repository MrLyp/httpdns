package vip.irock.httpdns

import java.lang.Exception
import java.net.InetAddress
import kotlin.math.max
import kotlin.math.min

data class PingStats(
    val transmitted: Int,
    val packetsLost: Int,
    val averageTime: Long,
    val minTime: Long,
    val maxTime: Long
) {

    override fun toString(): String {
        return "PingStats(transmitted=$transmitted, packetsLost=$packetsLost, averageTime=$averageTime, minTime=$minTime, maxTime=$maxTime)"
    }
}

class Ping(
    private val ip: String,
    private val times: Int,
    private val timeout: Int
) {

    fun ping(): PingStats {
        val address = InetAddress.getByName(ip)
        var count: Int = times
        var minTime: Long = Long.MAX_VALUE
        var maxTime: Long = -1
        var totalTime: Long = -1
        var transmitted: Int = 0
        var lost: Int = 0
        while (count-- > 0) {
            val startTime = System.currentTimeMillis()
            try {
                val reachable = address.isReachable(timeout)
                if (reachable) {
                    transmitted++
                    val roundTime = System.currentTimeMillis() - startTime
                    minTime = min(minTime, roundTime)
                    maxTime = max(maxTime, roundTime)
                    totalTime += roundTime
                } else {
                    lost++
                }
            } catch (e: Exception) {
                lost++
            }
        }
        val averageTime = if (transmitted == 0) {
            0
        } else {
            totalTime / transmitted
        }
        return PingStats(transmitted, lost, averageTime, minTime, maxTime)
    }
}