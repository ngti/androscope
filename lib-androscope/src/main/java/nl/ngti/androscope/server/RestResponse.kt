package nl.ngti.androscope.server

import android.database.Cursor
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.common.LOG
import nl.ngti.androscope.responses.*
import nl.ngti.androscope.utils.getRootFile
import nl.ngti.androscope.utils.resolveFileSystemType
import java.io.IOException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.min

class RestResponse : BaseAndroscopeResponse() {

    private val tag = javaClass.simpleName

    private val gson = Gson()

    override fun getResponse(session: SessionParams): NanoHTTPD.Response? {
        val restUrl = session.relativePath

        if (LOG) Log.d(tag, "Rest url: $restUrl")

        val start = SystemClock.elapsedRealtimeNanos()
        val responseObject: Any? =
                when (restUrl) {
                    "provider/data" -> getData(session)
                    "provider/metadata" -> getMetadata(session)
                    "file-system/list" -> getFileSystemList(session)
                    "file-system/count" -> getFileSystemCount(session)
                    "file-system/breadcrumbs" -> getFileSystemBreadcrumbs(session)
                    "file-system/delete" -> null
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

    private fun getFileSystemList(session: SessionParams): List<FileSystemEntry> {
        val root = getRootFile(context, session)
        val pageSize = session["pageSize"]?.toInt()
                ?: throw IllegalArgumentException("Missing page number")
        val pageNumber = session["pageNumber"]?.toInt() ?: 0

        val folderComparator = Comparator<FileSystemEntry> { entry1, entry2 ->
            entry2.isFolder.compareTo(entry1.isFolder)
        }
        val comparator = session["sortOrder"]?.let { order ->
            session["sortColumn"]?.let { column ->
                val compareFunction: (FileSystemEntry, FileSystemEntry) -> Int = when (column) {
                    "name" -> { entry1, entry2 -> entry1.name.compareTo(entry2.name) }
                    "extension" -> { entry1, entry2 -> entry1.extension.orEmpty().compareTo(entry2.extension.orEmpty()) }
                    "date" -> { entry1, entry2 -> entry1.dateInternal.compareTo(entry2.dateInternal) }
                    "size" -> { entry1, entry2 -> entry1.sizeInternal.compareTo(entry2.sizeInternal) }
                    else -> throw IllegalArgumentException("Illegal sort column: $column")
                }
                Comparator<FileSystemEntry>(compareFunction).let {
                    if (order == "desc") Collections.reverseOrder(it) else it
                }
            }
        }?.let {
            folderComparator.then(it)
        } ?: folderComparator

        val responseFactory = FileSystemListResponseFactory(context)
        return responseFactory.generate(root).sortedWith(comparator).run {
            val fromIndex = pageSize * pageNumber
            val toIndex = min(fromIndex + pageSize, size)
            subList(fromIndex, toIndex)
        }.apply {
            forEach {
                it.prepareForSerialization(responseFactory)
            }
        }
    }

    private fun getFileSystemCount(session: SessionParams): FileSystemCount {
        val root = getRootFile(context, session)
        val list = root.list()

        return FileSystemCount(list?.size ?: 0)
    }

    private fun getFileSystemBreadcrumbs(session: SessionParams): List<Breadcrumb> {
        val root = resolveFileSystemType(context, session)
        val result = ArrayList<Breadcrumb>()
        result += Breadcrumb(root.absolutePath, "")
        session["path"]?.run {
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
