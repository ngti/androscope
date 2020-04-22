package nl.ngti.androscope.responses.database

class Database(
        val name: String,
        val title: String = name,
        val description: String? = null,
        val error: String? = null
)
