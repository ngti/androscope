package nl.ngti.androscope.responses.provider

import android.database.Cursor
import android.net.Uri
import nl.ngti.androscope.responses.common.Refreshable
import nl.ngti.androscope.responses.common.ResponseDataCache
import nl.ngti.androscope.responses.common.UriDataProvider
import nl.ngti.androscope.server.*
import kotlin.math.min

internal class ProviderResponse(
        private val uriDataProvider: UriDataProvider
) {

    private val cursorCache = ResponseDataCache(
            paramsSupplier = ::CursorParams,
            dataSupplier = {
                queryCursor(it.uri, it.sortOrder)?.let { cursor ->
                    ResponseCursor(cursor)
                }
            },
            canUseData = {
                it != null && !it.isClosed
            },
            onAbandonData = {
                it?.close()
            }
    )

    fun getInfo(session: SessionParams): ProviderInfo? {
        return try {
            val uri = session.providerUri
            val cursor = cursorCache[session]
                    ?: throw IllegalStateException("Cannot query uri: $uri")
            cursor.run {
                ProviderInfo(
                        columns = columnNames,
                        rowCount = count
                )
            }
        } catch (e: Throwable) {
            ProviderInfo(
                    errorMessage = e.message
            )
        }
    }

    fun getData(session: SessionParams): ArrayList<ArrayList<String?>>? {
        val pageSize = session.pageSize
        val pageNumber = session.pageNumber

        val cursor = cursorCache[session] ?: return null
        val startPosition = pageSize * pageNumber

        if (!cursor.moveToPosition(startPosition)) {
            return null
        }

        val columnCount = cursor.columnCount
        val resultSize = min(pageSize, cursor.count - startPosition)

        return ArrayList<ArrayList<String?>>(resultSize).apply {
            synchronized(cursor) {
                var addedRows = 0
                do {
                    val row = ArrayList<String?>(columnCount)

                    for (i in 0 until columnCount) {
                        row.add(cursor[i])
                    }

                    add(row)
                    addedRows++

                    if (addedRows == pageSize) {
                        break
                    }
                } while (cursor.moveToNext())
            }
        }
    }

    private fun queryCursor(
            uri: Uri,
            sortOrder: String? = null
    ): Cursor? {
        return uriDataProvider.query(uri, sortOrder = sortOrder)
    }
}

private data class CursorParams(
        val uri: Uri,
        override val timestamp: Long,
        val sortOrder: String?
) : Refreshable {
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
