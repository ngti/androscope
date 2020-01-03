package nl.ngti.androscope.responses

class MetadataResponse(
        val columns: Array<String> = emptyArray(),
        val rowCount: Int = 0,
        val errorMessage: String? = null
)