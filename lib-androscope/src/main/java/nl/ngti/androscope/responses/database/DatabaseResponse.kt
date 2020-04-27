package nl.ngti.androscope.responses.database

import android.content.Context
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.get
import nl.ngti.androscope.utils.AndroscopeMetadata

class DatabaseResponse(
        private val context: Context,
        private val metadata: AndroscopeMetadata
) {

    fun getList(): List<Database> {
        val result = ArrayList<Database>()

        metadata.databaseName?.run {
            if (isNotBlank()) {
                val manifestDbConfig = DbConfig(context, this)
                result += manifestDbConfig.toJsonDatabase()
            }
        }

        context.databaseList().forEach {
            result += Database(it)
        }
        return result
    }

    fun getInfo(sessionParams: SessionParams): DatabaseInfo {
        val config = sessionParams.dbConfig
        return DatabaseInfo(config.name, "")
    }

    private val SessionParams.dbConfig get() = DbConfig(context, this["location"]!!)
}
