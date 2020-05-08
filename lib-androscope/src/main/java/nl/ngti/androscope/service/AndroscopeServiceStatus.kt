package nl.ngti.androscope.service

internal class AndroscopeServiceStatus private constructor(
        val message: String,
        private val status: Int
) {

    val isRestartNeeded: Boolean
        get() = status != STATUS_OK

    companion object {
        private const val STATUS_OK = 0
        private const val STATUS_FAILED = 1
        private const val STATUS_STOPPED = 2

        fun success(message: String): AndroscopeServiceStatus {
            return AndroscopeServiceStatus(message, STATUS_OK)
        }

        fun error(message: String): AndroscopeServiceStatus {
            return AndroscopeServiceStatus(message, STATUS_FAILED)
        }

        fun stopped(message: String): AndroscopeServiceStatus {
            return AndroscopeServiceStatus(message, STATUS_STOPPED)
        }
    }

}
