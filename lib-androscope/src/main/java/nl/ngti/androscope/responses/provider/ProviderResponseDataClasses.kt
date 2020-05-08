package nl.ngti.androscope.responses.provider

internal class ProviderInfo(
        val columns: Array<String> = emptyArray(),
        val rowCount: Int = 0,
        val errorMessage: String? = null
)
