package nl.ngti.androscope.responses.database

import android.content.Context
import android.os.Bundle

private const val KEY_DATABASE_NAME = "nl.ngti.androscope.DATABASE_NAME"

class DatabaseResponse(
        private val context: Context,
        private val metadata: Bundle
) {

    fun getList(): List<Database> {
        val result = ArrayList<Database>()

        metadata.getString(KEY_DATABASE_NAME)?.run {
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
