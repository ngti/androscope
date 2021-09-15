package nl.ngti.androscope.responses.files

import nl.ngti.androscope.responses.common.indexOfExtension
import java.io.File

private const val NOT_INITIALIZED = -1L

internal interface IFormatter {

    fun formatFileSize(size: Long): String

    fun formatDate(timestamp: Long): String
}

internal class FileSystemEntry internal constructor(
        root: File,
        itemName: String
) {
    @Transient
    private val file = File(root, itemName)

    @JvmField
    val isFolder = file.isDirectory

    val name: String
    val extension: String

    @Volatile
    lateinit var date: String
    var size: String? = null

    @Transient
    private var dateOnDemand: Long = NOT_INITIALIZED

    val dateAsLong: Long
        get() {
            if (dateOnDemand == NOT_INITIALIZED) {
                dateOnDemand = file.lastModified()
            }
            return dateOnDemand
        }

    @Transient
    private var sizeOnDemand: Long = if (isFolder) 0 else NOT_INITIALIZED

    val sizeAsLong: Long
        get() {
            if (sizeOnDemand == NOT_INITIALIZED) {
                sizeOnDemand = file.length()
            }
            return sizeOnDemand
        }

    init {
        if (isFolder) {
            name = itemName
            extension = ""
        } else {
            val extensionIndex = indexOfExtension(itemName)
            if (extensionIndex < 1) {
                name = itemName
                extension = ""
            } else {
                name = itemName.substring(0, extensionIndex)
                extension = itemName.substring(extensionIndex + 1)
            }
        }
    }

    fun prepareForSerialization(formatter: IFormatter) {
        if (!::date.isInitialized) {
            date = formatter.formatDate(dateAsLong)
            if (!isFolder) {
                size = formatter.formatFileSize(sizeAsLong)
            }
        }
    }
}

internal class FileSystemCount(
        val totalEntries: Int
)

internal class Breadcrumb(
        val name: String,
        val path: String
)
