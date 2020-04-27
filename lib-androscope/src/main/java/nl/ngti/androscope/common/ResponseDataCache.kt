package nl.ngti.androscope.common

import nl.ngti.androscope.server.SessionParams

class ResponseDataCache<Params, Data>(
        private val paramsSupplier: (SessionParams) -> Params,
        private val dataSupplier: (Params) -> Data,
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
            if (lastParams == params) {
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
