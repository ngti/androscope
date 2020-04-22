package nl.ngti.androscope.responses.database

class Database(
        val path: String,
        val title: String = path,
        val description: String? = null,
        val error: String? = null
)
