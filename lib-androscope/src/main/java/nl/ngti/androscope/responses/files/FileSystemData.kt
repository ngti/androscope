package nl.ngti.androscope.responses.files

import android.content.Context
import nl.ngti.androscope.server.*
import java.io.File
import kotlin.math.min

internal class FileSystemData(
        context: Context,
        private val root: File
) {

    private val responseFactory = FileSystemEntryListFactory(context)

    private val files = root.list() ?: emptyArray()

    private lateinit var fileEntries: Array<FileSystemEntry>

    private val fileCount
        get() = files.size

    private var lastSortParams: SortParams? = null

    fun getFileSystemList(session: SessionParams): Array<FileSystemEntry> {
        val pageSize = session.pageSize
        val pageNumber = session.pageNumber

        val sortParams = SortParams(session.sortOrder, session.sortColumn)

        val fromIndex = pageSize * pageNumber
        return getEntries(sortParams, fromIndex, pageSize).apply {
            forEach {
                it.prepareForSerialization(responseFactory)
            }
        }
    }

    fun getFileSystemCount(): FileSystemCount {
        return FileSystemCount(fileCount)
    }

    private fun getEntries(sortParams: SortParams, fromIndex: Int, pageSize: Int): Array<FileSystemEntry> {
        synchronized(this) {
            if (!::fileEntries.isInitialized) {
                fileEntries = responseFactory.generate(root, files)
            }
            if (lastSortParams != sortParams) {
                fileEntries.sortWith(sortParams.comparator)
                lastSortParams = sortParams
            }
            val toIndex = min(fromIndex + pageSize, fileEntries.size)
            return fileEntries.copyOfRange(fromIndex, toIndex)
        }
    }
}
