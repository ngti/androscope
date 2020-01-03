package nl.ngti.androscope.server

import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.responses.MetadataResponse
import java.io.IOException

class RestResponse : BaseAndroscopeResponse() {

    private val tag = javaClass.simpleName

    private val gson = Gson()

    override fun getResponse(session: SessionParams): NanoHTTPD.Response? {
        val restUrl = session.relativePath

        val uri = Uri.parse(session["uri"] ?: throw IOException("No uri provided"))

        if (LOG) Log.d(tag, "Rest url: $restUrl, uri: $uri")

        val responseObject: Any? =
                when (restUrl) {
                    "data" -> getData(uri, session)
                    "metadata" -> getMetadata(uri)
                    else -> throw IOException("Unknown path: ${session.path}")
                }

        val json = gson.toJson(responseObject)

        if (LOG) Log.d(tag, "Response: $json")

        return NanoHTTPD.newFixedLengthResponse(json)
    }

    private fun <R> processCursor(
            uri: Uri,
            sortOrder: String? = null,
            onSuccess: (Cursor) -> R?,
            onError: ((Throwable) -> R?)? = null
    ): R? {
        return try {
            context.contentResolver.query(uri, null, null, null, sortOrder)?.use(onSuccess)
                    ?: throw IllegalStateException("Cannot query uri: $uri")
        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }

    private fun getMetadata(uri: Uri): MetadataResponse? {
        return processCursor(uri,
                onSuccess = {
                    MetadataResponse(
                            columns = it.columnNames,
                            rowCount = it.count
                    )
                },
                onError = {
                    MetadataResponse(
                            errorMessage = it.message
                    )
                }
        )
    }

    private fun getData(uri: Uri, session: SessionParams): ArrayList<ArrayList<String>>? {
        val pageSize = session["pageSize"]?.toInt()
                ?: throw IllegalArgumentException("Missing page number")
        val pageNumber = session["pageNumber"]?.toInt() ?: 0
        val sortOrder = session["sortOrder"]?.let { order ->
            session["sortColumn"]?.let { column ->
                "$column $order"
            }
        }

        return processCursor(uri,
                sortOrder = sortOrder,
                onSuccess = {
                    if (!it.moveToPosition(pageSize * pageNumber)) {
                        return@processCursor null
                    }

                    val columnCount = it.columnCount

                    ArrayList<ArrayList<String>>(it.count).apply {
                        while (it.moveToNext()) {
                            val row = ArrayList<String>()

                            for (i in 0 until columnCount) {
                                row.add(it.getString(i))
                            }

                            add(row)

                            if (size == pageSize) {
                                break
                            }
                        }
                    }
                }
        )
    }
}
