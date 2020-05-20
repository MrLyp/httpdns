package vip.irock.httpdns

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object DnsConfig {

    private var executor: ExecutorService? = null

    private val defaultExecutor: ExecutorService by lazy { Executors.newCachedThreadPool() }

    private var aliyunAccountId: String? = null

    fun setExecutorService(e: ExecutorService) {
        this.executor = e
    }

    fun getExecutors(): ExecutorService {
        return executor ?: defaultExecutor
    }

    fun setAliyunAccount(account: String) {
        aliyunAccountId = account
    }

    fun getAliyunAccount(): String? {
        return aliyunAccountId
    }
}