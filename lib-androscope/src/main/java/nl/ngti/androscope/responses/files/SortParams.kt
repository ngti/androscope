package nl.ngti.androscope.responses.files

import java.lang.IllegalArgumentException
import java.util.Collections
import kotlin.Comparator

internal data class SortParams(
    private val sortOrder: String?,
    private val sortColumn: String?
) {

    val comparator: Comparator<FileSystemEntry>
        get() {
            var result = folderComparator
            sortOrderComparator?.let {
                result = result then it
            }
            return result
        }

    private val sortOrderComparator: Comparator<FileSystemEntry>?
        get() =
            sortOrder?.let { order ->
                sortColumn?.let { column ->
                    getSortingComparator(order, column)
                }
            }

    companion object {
        val folderComparator: Comparator<FileSystemEntry>
            get() = compare(valueSelector = {
                it.isFolder
            }) { a, b ->
                // Folders should be shown first
                java.lang.Boolean.compare(b, a)
            }

        fun getSortingComparator(order: String, column: String): Comparator<FileSystemEntry> {
            val nameComparator = compareStrings(FileSystemEntry::name)
            val mainComparator: Comparator<FileSystemEntry> = when (column) {
                "name" -> nameComparator
                "extension" -> compareStrings(FileSystemEntry::extension)
                "date" -> compareLong(FileSystemEntry::dateAsLong)
                "size" -> compareLong(FileSystemEntry::sizeAsLong)
                else -> throw IllegalArgumentException("Illegal sort column: $column")
            }
            var comparator: Comparator<FileSystemEntry> = if (mainComparator !== nameComparator) {
                mainComparator then nameComparator
            } else {
                mainComparator
            }
            if ("desc" == order) {
                comparator = Collections.reverseOrder(comparator)
            }
            return comparator
        }

        private fun compareStrings(selector: FileSystemEntryValueSelector<String>) = compare(selector) { a, b ->
            a.compareTo(b, ignoreCase = true)
        }

        private fun compareLong(selector: FileSystemEntryValueSelector<Long>) = compare(selector) { a, b ->
            a.compareTo(b)
        }

        private inline fun <T> compare(
            valueSelector: FileSystemEntryValueSelector<T>,
            crossinline compareBlock: (a: T, b: T) -> Int
        ): Comparator<FileSystemEntry> {
            return Comparator { a: FileSystemEntry, b: FileSystemEntry ->
                compareBlock(valueSelector(a), valueSelector(b))
            }
        }

        private fun interface FileSystemEntryValueSelector<T> {
            operator fun invoke(entry: FileSystemEntry): T
        }
    }
}
