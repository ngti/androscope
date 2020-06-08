package nl.ngti.androscope.responses.common

import nl.ngti.androscope.server.SessionParams

internal class ResponseDataCache<Params : Refreshable, Data>(
        private val paramsSupplier: (SessionParams) -> Params,
        private val dataSupplier: (Params) -> Data,
        private val canUseData: (Data?) -> Boolean = { it != null },
        private val onAbandonData: ((Data?) -> Unit)? = null
) {

    private var lastParams: Params? = null
    private var lastCachedData: Data? = null
        set(value) {
            if (field !== value) {
                val oldData = field
                field = value
                onAbandonData?.run {
                    invoke(oldData)
                }
            }
        }

    operator fun get(session: SessionParams): Data {
        val params = paramsSupplier(session)
        return synchronized(this) {
            if (lastParams == params && canUseData(lastCachedData)) {
                lastCachedData!!
            } else {
                lastParams = params
                dataSupplier(params).apply {
                    lastCachedData = this
                }
            }
        }
    }
}

/**
 * Enforces parameters used in [ResponseDataCache] to contain [timestamp] property that allows to
 * invalidate cache when user refreshes a web page.
 */
internal interface Refreshable {

    /**
     * Should be passed from a web page. When user refreshes the web page, a new timestamp
     * will be passed, so the cache will get invalidated.
     */
    val timestamp: Long
}
