package nl.ngti.androscope.responses.database

class Database(
        val name: String,
        val title: String = name,
        val description: String? = null,
        val error: String? = null
)

class DatabaseInfo(
        val valid: Boolean,
        val errorMessage: String? = null,
        val fullPath: String? = null,
        val size: String? = null
) {
    val tables = ArrayList<String>()
    val views = ArrayList<String>()
    val triggers = ArrayList<String>()
    val indexes = ArrayList<String>()
}

class SqlParams(
        val sql: String
)
