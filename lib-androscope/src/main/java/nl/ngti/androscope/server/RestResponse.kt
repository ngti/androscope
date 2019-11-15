package nl.ngti.androscope.server

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.common.PATH_REST
import nl.ngti.androscope.responses.RowCountResponse
import java.io.IOException

class RestResponse : BaseAndroscopeResponse() {

    private val tag = javaClass.simpleName

    private val gson = Gson()

    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val path = session.uri
        val restUrl = path.substringAfter(PATH_REST)

        val uri = Uri.parse(session.parameters["uri"]?.get(0)
                ?: throw IOException("No uri provided"))

        if (LOG) Log.d(tag, "Rest url: $restUrl, uri: $uri")

        val json =
                when (restUrl) {
                    "columns" -> getColumns(uri)
                    "data" -> getData(uri)
                    "row-count" -> getRowCount(uri)
                    else -> null
                } ?: throw IOException("Unknown path: $path")

        if (LOG) Log.d(tag, "Response: $json")

        return NanoHTTPD.newFixedLengthResponse(json)
    }

    private fun getCursor(uri: Uri) =
            context.contentResolver.query(uri, null, null, null, null)

    private fun getColumns(uri: Uri) =
            getCursor(uri)?.use {
                gson.toJson(it.columnNames)
            }

    private fun getRowCount(uri: Uri) =
            getCursor(uri)?.use {
                gson.toJson(RowCountResponse(it.count))
            }

    private fun getData(uri: Uri) =
            getCursor(uri)?.use {
                val result = ArrayList<ArrayList<String>>(it.count)
                val columnCount = it.columnCount

                while (it.moveToNext()) {
                    val row = ArrayList<String>()

                    for (i in 0 until columnCount) {
                        row.add(it.getString(i))
                    }

                    result.add(row)

                    if (result.size > 100) {
                        break
                    }
                }

                gson.toJson(result)
            }

}
