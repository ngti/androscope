package nl.ngti.androscope.responses.files

import android.content.Context
import nl.ngti.androscope.server.*
import java.io.File
import java.util.*
import kotlin.Comparator
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

private data class SortParams(
        val sortOrder: String?,
        val sortColumn: String?
) {

    val comparator: Comparator<FileSystemEntry>
        get() {
            val nameCompareFunction: (FileSystemEntry, FileSystemEntry) -> Int = { entry1, entry2 ->
                entry1.name.compareTo(entry2.name, ignoreCase = true)
            }
            val folderComparator = Comparator<FileSystemEntry> { entry1, entry2 ->
                entry2.isFolder.compareTo(entry1.isFolder)
            }
            return sortOrder?.let { order ->
                sortColumn?.let { column ->
                    val compareFunction: (FileSystemEntry, FileSystemEntry) -> Int = when (column) {
                        "name" -> nameCompareFunction
                        "extension" -> { entry1, entry2 -> entry1.extension.orEmpty().compareTo(entry2.extension.orEmpty()) }
                        "date" -> { entry1, entry2 -> entry1.dateInternal.compareTo(entry2.dateInternal) }
                        "size" -> { entry1, entry2 -> entry1.sizeInternal.compareTo(entry2.sizeInternal) }
                        else -> throw IllegalArgumentException("Illegal sort column: $column")
                    }
                    val comparator = Comparator<FileSystemEntry>(compareFunction).let {
                        if (order == "desc") Collections.reverseOrder(it) else it
                    }
                    if (compareFunction !== nameCompareFunction) {
                        comparator.then(Comparator(nameCompareFunction))
                    } else comparator
                }
            }?.let {
                folderComparator.then(it)
            } ?: folderComparator
        }
}
