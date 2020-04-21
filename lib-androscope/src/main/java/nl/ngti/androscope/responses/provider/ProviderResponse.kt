package nl.ngti.androscope.responses.provider

import android.content.Context
import android.database.Cursor
import android.net.Uri
import nl.ngti.androscope.server.SessionParams
import nl.ngti.androscope.server.get
import java.io.IOException

class ProviderResponse(
        private val context: Context
) {

    fun getMetadata(session: SessionParams): MetadataResponse? {
        return processCursor(session,
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

    fun getData(session: SessionParams): ArrayList<ArrayList<String>>? {
        val pageSize = session["pageSize"]?.toInt()
                ?: throw IllegalArgumentException("Missing page number")
        val pageNumber = session["pageNumber"]?.toInt() ?: 0
        val sortOrder = session["sortOrder"]?.let { order ->
            session["sortColumn"]?.let { column ->
                "$column $order"
            }
        }

        return processCursor(session,
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

    private fun getUri(session: SessionParams): Uri =
            Uri.parse(session["uri"] ?: throw IOException("No uri provided"))

    private fun <R> processCursor(
            session: SessionParams,
            sortOrder: String? = null,
            onSuccess: (Cursor) -> R?,
            onError: ((Throwable) -> R?)? = null
    ): R? {
        return try {
            val uri = getUri(session)
            context.contentResolver.query(uri, null, null, null, sortOrder)?.use(onSuccess)
                    ?: throw IllegalStateException("Cannot query uri: $uri")
        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }
}
