package nl.ngti.androscope.server

import android.content.Context
import nl.ngti.androscope.responses.files.FileSystemCount
import nl.ngti.androscope.responses.files.FileSystemEntry
import nl.ngti.androscope.responses.files.FileSystemEntryListFactory
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.math.min

class FileSystemData(
        context: Context,
        private val root: File
) {

    private val responseFactory = FileSystemEntryListFactory(context)

    private val fileList: Array<String>? by lazy {
        root.list()
    }

    private val fileCount
        get() = fileList?.size ?: 0

    private var fileEntryList: List<FileSystemEntry>? = null

    private var lastSortParams: SortParams? = null

    fun getFileSystemList(session: SessionParams): List<FileSystemEntry> {
        val pageSize = session["pageSize"]?.toInt()
                ?: throw IllegalArgumentException("Missing page number")
        val pageNumber = session["pageNumber"]?.toInt() ?: 0

        val sortParams = SortParams(session["sortOrder"], session["sortColumn"])

        return getEntryList(sortParams).run {
            val fromIndex = pageSize * pageNumber
            val toIndex = min(fromIndex + pageSize, size)
            subList(fromIndex, toIndex)
        }.apply {
            forEach {
                it.prepareForSerialization(responseFactory)
            }
        }
    }

    fun getFileSystemCount(): FileSystemCount {
        return FileSystemCount(fileCount)
    }

    private fun getEntryList(sortParams: SortParams): List<FileSystemEntry> {
        synchronized(this) {
            var entryList = fileEntryList
            if (entryList == null) {
                entryList = responseFactory.generate(root)
            }
            if (lastSortParams != sortParams) {
                entryList = entryList.sortedWith(sortParams.comparator)
                lastSortParams = sortParams
            }
            fileEntryList = entryList
            return entryList
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
