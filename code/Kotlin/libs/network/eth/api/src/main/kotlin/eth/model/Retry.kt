package eth.model

/**
 * 网络重试的参数配置
 */
data class Retry(var count: Int = 3, var delay: Long = 1000)
