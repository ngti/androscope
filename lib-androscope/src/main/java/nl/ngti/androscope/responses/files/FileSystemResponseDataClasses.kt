package nl.ngti.androscope.responses.files

import nl.ngti.androscope.responses.common.indexOfExtension
import java.io.File

internal interface IFormatter {

    fun formatFileSize(size: Long): String

    fun formatDate(timestamp: Long): String
}

internal class FileSystemEntry internal constructor(
        root: File,
        itemName: String
) {
    val name: String
    val extension: String?
    val isFolder: Boolean

    @Volatile
    lateinit var date: String
    var size: String? = null

    @Transient
    val dateInternal: Long

    @Transient
    val sizeInternal: Long

    init {
        val file = File(root, itemName)
        isFolder = file.isDirectory
        dateInternal = file.lastModified()
        sizeInternal = if (isFolder) -1 else file.length()
        if (isFolder) {
            name = itemName
            extension = null
        } else {
            val extensionIndex = indexOfExtension(itemName)
            if (extensionIndex < 1) {
                name = itemName
                extension = null
            } else {
                name = itemName.substring(0, extensionIndex)
                extension = itemName.substring(extensionIndex + 1)
            }
        }
    }

    fun prepareForSerialization(formatter: IFormatter) {
        if (!::date.isInitialized) {
            date = formatter.formatDate(dateInternal)
            if (!isFolder) {
                size = formatter.formatFileSize(sizeInternal)
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
