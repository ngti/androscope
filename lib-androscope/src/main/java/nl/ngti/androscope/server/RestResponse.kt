package nl.ngti.androscope.server

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.responses.RowCountResponse
import java.io.IOException

class RestResponse : BaseAndroscopeResponse() {

    private val tag = javaClass.simpleName

    private val gson = Gson()

    override fun getResponse(session: SessionParams): NanoHTTPD.Response? {
        val restUrl = session.relativePath

        val uri = Uri.parse(session["uri"] ?: throw IOException("No uri provided"))

        if (LOG) Log.d(tag, "Rest url: $restUrl, uri: $uri")

        val json =
                when (restUrl) {
                    "columns" -> getColumns(uri)
                    "data" -> getData(uri, session)
                    "row-count" -> getRowCount(uri)
                    else -> null
                } ?: throw IOException("Unknown path: ${session.path}")

        if (LOG) Log.d(tag, "Response: $json")

        return NanoHTTPD.newFixedLengthResponse(json)
    }

    private fun getCursor(uri: Uri, sortOrder: String? = null) =
            context.contentResolver.query(uri, null, null, null, sortOrder)

    private fun getColumns(uri: Uri) =
            getCursor(uri)?.use {
                gson.toJson(it.columnNames)
            }

    private fun getRowCount(uri: Uri) =
            getCursor(uri)?.use {
                gson.toJson(RowCountResponse(it.count))
            }

    private fun getData(uri: Uri, session: SessionParams): String? {
        val pageSize = session["pageSize"]?.toInt()
                ?: throw IllegalArgumentException("Missing page number")
        val pageNumber = session["pageNumber"]?.toInt() ?: 0
        val sortOrder = session["sortOrder"]?.let { order ->
            session["sortColumn"]?.let { column ->
                "$column $order"
            }
        }

        return getCursor(uri, sortOrder)?.use {
            val result = ArrayList<ArrayList<String>>(it.count)
            val columnCount = it.columnCount

            if (!it.moveToPosition(pageSize * pageNumber)) {
                return null
            }

            while (it.moveToNext()) {
                val row = ArrayList<String>()

                for (i in 0 until columnCount) {
                    row.add(it.getString(i))
                }

                result.add(row)

                if (result.size == pageSize) {
                    break
                }
            }

            gson.toJson(result)
        }
    }


}
