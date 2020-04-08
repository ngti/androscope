package nl.ngti.androscope.responses

import android.content.Context
import android.text.format.Formatter
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal interface IFormatter {

    fun formatFileSize(size: Long): String

    fun formatDate(timestamp: Long): String
}

class FileSystemEntry internal constructor(
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
            val extensionIndex = FilenameUtils.indexOfExtension(itemName)
            if (extensionIndex < 1) {
                name = itemName
                extension = null
            } else {
                name = itemName.substring(0, extensionIndex)
                extension = itemName.substring(extensionIndex + 1)
            }
        }
    }

    internal fun prepareForSerialization(formatter: IFormatter) {
        if (!::date.isInitialized) {
            date = formatter.formatDate(dateInternal)
            if (!isFolder) {
                size = formatter.formatFileSize(sizeInternal)
            }
        }
    }
}

class FileSystemListResponseFactory(
        private val context: Context
) : IFormatter {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    fun generate(root: File): List<FileSystemEntry> =
            root.list()?.let { array ->
                ArrayList<FileSystemEntry>(array.size).also { result ->
                    array.forEach {
                        result += FileSystemEntry(root, it)
                    }
                }
            } ?: emptyList()

    override fun formatFileSize(size: Long): String = Formatter.formatFileSize(context, size)

    override fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))

}

class FileSystemCount(
        val totalEntries: Int
)

class Breadcrumb(
        val name: String,
        val path: String
)
