package nl.ngti.androscope.responses.database

import android.content.Context
import nl.ngti.androscope.utils.AndroscopeMetadata


class DatabaseResponse(
        private val context: Context,
        private val metadata: AndroscopeMetadata
) {

    fun getList(): List<Database> {
        val result = ArrayList<Database>()

        metadata.databaseName?.run {
            if (isNotBlank()) {
                result += Database(this, "Set in manifest")
            }
        }

        context.databaseList().forEach {
            result += Database(it)
        }
        return result
    }
}
