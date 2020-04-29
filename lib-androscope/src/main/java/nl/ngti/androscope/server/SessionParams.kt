package nl.ngti.androscope.server

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.database.DbUri
import nl.ngti.androscope.responses.database.SqlParams
import java.io.IOException
import java.io.InputStreamReader

typealias SessionParams = NanoHTTPD.IHTTPSession

val SessionParams.path: String
    get() = uri

operator fun SessionParams.get(key: String): String? =
        parameters[key]?.get(0)

val SessionParams.timestamp: Long
    get() = this["timestamp"]?.toLong() ?: 0

val SessionParams.pageSize: Int
    get() = this["pageSize"]?.toInt()
            ?: throw IllegalArgumentException("Missing page size")

val SessionParams.pageNumber: Int
    get() = this["pageNumber"]?.toInt() ?: 0

val SessionParams.sortOrder: String?
    get() = this["sortOrder"]

val SessionParams.sortColumn: String?
    get() = this["sortColumn"]

val SessionParams.providerUri: Uri
    get() = Uri.parse(this["uri"] ?: throw IOException("No uri provided"))

val SessionParams.dbUri: DbUri
    get() = DbUri(providerUri)

fun SessionParams.readSql(jsonConverter: Gson): String {
    return JsonReader(InputStreamReader(inputStream)).let {
        jsonConverter.fromJson<SqlParams>(it, SqlParams::class.java).sql
    }
}
