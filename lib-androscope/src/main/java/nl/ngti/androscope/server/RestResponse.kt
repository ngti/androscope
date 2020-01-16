package nl.ngti.androscope.server

import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.responses.FileSystemCount
import nl.ngti.androscope.responses.FileSystemEntry
import nl.ngti.androscope.responses.FileSystemListResponseFactory
import nl.ngti.androscope.responses.MetadataResponse
import java.io.File
import java.io.IOException

class RestResponse : BaseAndroscopeResponse() {

    private val tag = javaClass.simpleName

    private val gson = Gson()

    override fun getResponse(session: SessionParams): NanoHTTPD.Response? {
        val restUrl = session.relativePath

        if (LOG) Log.d(tag, "Rest url: $restUrl")

        val responseObject: Any? =
                when (restUrl) {
                    "provider/data" -> getData(session)
                    "provider/metadata" -> getMetadata(session)
                    "file-system/list" -> getFileSystemList(session)
                    "file-system/breadcrumbs" -> getFileSystemBreadcrumbs(session)
                    "file-system/count" -> getFileSystemCount(session)
                    "file-system/delete" -> null
                    else -> throw IOException("Unknown path: ${session.path}")
                }

        val json = gson.toJson(responseObject)

        if (LOG) Log.d(tag, "Response: $json")

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

    private fun getFileSystemList(session: SessionParams): List<FileSystemEntry> {
        val root = getRootFile(session)

        return FileSystemListResponseFactory(context).generate(root)
    }

    private fun getFileSystemCount(session: SessionParams): FileSystemCount {
        val root = getRootFile(session)
        val list = root.list()

        return FileSystemCount(list?.size ?: 0)
    }

    private fun getRootFile(session: SessionParams): File {
        val dataDir = context.applicationInfo.dataDir
        return session["path"]?.let {
            File(dataDir, it)
        } ?: File(dataDir)
    }

    // FIXME
    private fun getFileSystemBreadcrumbs(session: SessionParams) = null
}
