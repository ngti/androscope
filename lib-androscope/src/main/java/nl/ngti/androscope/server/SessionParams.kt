package nl.ngti.androscope.server

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.database.DbUri
import nl.ngti.androscope.responses.database.SqlParams
import java.io.IOException
import java.io.InputStreamReader

internal typealias SessionParams = NanoHTTPD.IHTTPSession

internal val SessionParams.path: String
    get() = uri

internal operator fun SessionParams.get(key: String): String? =
        parameters[key]?.get(0)

internal val SessionParams.timestamp: Long
    get() = this["timestamp"]?.toLong() ?: 0

internal val SessionParams.pageSize: Int
    get() = this["pageSize"]?.toInt()
            ?: throw IllegalArgumentException("Missing page size")

internal val SessionParams.pageNumber: Int
    get() = this["pageNumber"]?.toInt() ?: 0

internal val SessionParams.sortOrder: String?
    get() = this["sortOrder"]

internal val SessionParams.sortColumn: String?
    get() = this["sortColumn"]

internal val SessionParams.providerUri: Uri
    get() = Uri.parse(this["uri"] ?: throw IOException("No uri provided"))

internal val SessionParams.dbUri: DbUri
    get() = DbUri(providerUri)

internal fun SessionParams.readSql(jsonConverter: Gson): String {
    return JsonReader(InputStreamReader(inputStream)).let {
        jsonConverter.fromJson<SqlParams>(it, SqlParams::class.java).sql
    }
}
