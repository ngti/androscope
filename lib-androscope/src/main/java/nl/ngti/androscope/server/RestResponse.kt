package nl.ngti.androscope.server

import android.database.Cursor
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.responses.Breadcrumb
import nl.ngti.androscope.responses.MetadataResponse
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class RestResponse : BaseAndroscopeResponse() {

    private val tag = javaClass.simpleName

    private val gson = Gson()

    private val fileSystemResponseCache by lazy {
        FileSystemResponseCache(context)
    }

    override fun getResponse(session: SessionParams): NanoHTTPD.Response? {
        val restUrl = session.relativePath

        if (LOG) Log.d(tag, "Rest url: $restUrl")

        val start = SystemClock.elapsedRealtimeNanos()
        val responseObject: Any? =
                when (restUrl) {
                    "provider/data" -> getData(session)
                    "provider/metadata" -> getMetadata(session)
                    "file-system/list" -> fileSystemResponseCache[session].getFileSystemList(session)
                    "file-system/count" -> fileSystemResponseCache[session].getFileSystemCount()
                    "file-system/breadcrumbs" -> getFileSystemBreadcrumbs(session)
                    "file-system/delete" -> null // FIXME
                    else -> throw IOException("Unknown path: ${session.path}")
                }

        if (LOG) Log.d(tag, String.format(Locale.ENGLISH,
                "Response generation time: %,d ns",
                SystemClock.elapsedRealtimeNanos() - start))

        val json = gson.toJson(responseObject)

//        if (LOG) Log.d(tag, "Response: $json")

        return NanoHTTPD.newFixedLengthResponse(json)
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

    private fun getMetadata(session: SessionParams): MetadataResponse? {
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

    private fun getData(session: SessionParams): ArrayList<ArrayList<String>>? {
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

    private fun getFileSystemBreadcrumbs(session: SessionParams): List<Breadcrumb> {
        val params = FileSystemParams(session)
        val root = params.resolveFileSystemType(context)
        val result = ArrayList<Breadcrumb>()
        result += Breadcrumb(root.absolutePath, "")
        params.path?.takeIf { it.isNotEmpty() }?.run {
            var relativePath = ""
            split('/').forEach {
                relativePath += it
                result += Breadcrumb(it, relativePath)
                relativePath += '/'
            }
        }
        return result
    }
}
