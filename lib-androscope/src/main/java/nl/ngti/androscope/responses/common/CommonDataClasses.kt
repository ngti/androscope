package nl.ngti.androscope.responses.common

internal class RequestResult private constructor(
        val success: Boolean,
        val message: String? = null
) {
    companion object {

        fun success(message: String? = null) = RequestResult(true, message)

        fun error(message: String? = null) = RequestResult(false, message)
    }
}
