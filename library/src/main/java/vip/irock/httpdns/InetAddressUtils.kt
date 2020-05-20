package vip.irock.httpdns

import java.util.regex.Pattern

object InetAddressUtils {
    private val IPV4_PATTERN: Pattern = Pattern.compile(
        "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"
    )
    private val IPV6_STD_PATTERN: Pattern = Pattern.compile(
        "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    )
    private val IPV6_HEX_COMPRESSED_PATTERN: Pattern = Pattern.compile(
        "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$"
    )

    fun isIPv4Address(input: String?): Boolean {
        return IPV4_PATTERN.matcher(input).matches()
    }

    fun isIPv6StdAddress(input: String?): Boolean {
        return IPV6_STD_PATTERN.matcher(input).matches()
    }

    fun isIPv6HexCompressedAddress(input: String?): Boolean {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches()
    }

    fun isIPv6Address(input: String?): Boolean {
        return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(
            input
        )
    }

    fun isNumericAddress(input: String?): Boolean {
        return isIPv4Address(input) or isIPv6Address(input)
    }
}