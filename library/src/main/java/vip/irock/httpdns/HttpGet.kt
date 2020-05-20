package vip.irock.httpdns

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpGet(private val url: String) {

    @Throws(IOException::class)
    fun get(): String? {
        var inputStream: InputStream? = null
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = RESOLVE_TIMEOUT
            conn.readTimeout = RESOLVE_TIMEOUT
            conn.requestMethod = "GET"
            conn.useCaches = false
            conn.allowUserInteraction = false
            if (conn.responseCode != 200) {
                return null
            }
            inputStream = conn.inputStream ?: return null
            val streamReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val sb = StringBuilder()
            var line: String? = streamReader.readLine()
            while (line != null) {
                sb.append(line)
                line = streamReader.readLine()
            }
            return sb.toString()
        } finally {
            inputStream?.close()
        }
    }

    companion object {
        const val RESOLVE_TIMEOUT = 5000
    }
}