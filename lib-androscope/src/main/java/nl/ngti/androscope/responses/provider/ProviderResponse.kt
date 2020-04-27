package nl.ngti.androscope.responses.provider

import android.content.Context
import android.database.Cursor
import android.net.Uri
import nl.ngti.androscope.common.ResponseDataCache
import nl.ngti.androscope.server.*
import java.io.IOException
import kotlin.math.min

class ProviderResponse(
        private val context: Context
) {

    private val cursorCache = ResponseDataCache(
            paramsSupplier = ::CursorParams,
            dataSupplier = {
                queryCursor(it.uri, it.sortOrder)
            },
            onAbandonData = {
                it?.close()
            }
    )

    fun getInfo(session: SessionParams): ProviderInfo? {
        return try {
            val uri = session.providerUri
            val cursor = queryCursor(uri) ?: throw IllegalStateException("Cannot query uri: $uri")
            cursor.use {
                ProviderInfo(
                        columns = it.columnNames,
                        rowCount = it.count
                )
            }
        } catch (e: Throwable) {
            ProviderInfo(
                    errorMessage = e.message
            )
        }
    }

    fun getData(session: SessionParams): ArrayList<ArrayList<String>>? {
        val pageSize = session.pageSize
        val pageNumber = session.pageNumber

        val cursor = cursorCache[session] ?: return null
        val startPosition = pageSize * pageNumber

        if (!cursor.moveToPosition(startPosition)) {
            return null
        }

        val columnCount = cursor.columnCount
        val resultSize = min(pageSize, cursor.count - startPosition)

        var addedRows = 0
        return ArrayList<ArrayList<String>>(resultSize).apply {
            while (cursor.moveToNext()) {
                val row = ArrayList<String>(columnCount)

                for (i in 0 until columnCount) {
                    row.add(cursor.getString(i))
                }

                add(row)
                addedRows++

                if (addedRows == pageSize) {
                    break
                }
            }
        }
    }

    private fun queryCursor(
            uri: Uri,
            sortOrder: String? = null
    ): Cursor? {
        return context.contentResolver.query(uri, null, null, null, sortOrder)
    }
}

private val SessionParams.providerUri: Uri
    get() = Uri.parse(this["uri"] ?: throw IOException("No uri provided"))

private data class CursorParams(
        val uri: Uri,
        private val timestamp: Long,
        val sortOrder: String?
) {
    constructor(session: SessionParams) : this(
            uri = session.providerUri,
            timestamp = session.timestamp,
            sortOrder = session.sortOrder?.let { order ->
                session.sortColumn?.let { column ->
                    "$column $order"
                }
            }
    )
}
