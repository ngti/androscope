@file:Suppress("MemberVisibilityCanBePrivate")

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

class FileSystemEntry {
    val name: String
    val extension: String?
    val isFolder: Boolean
    val date: String
    val size: String?

    internal constructor(root: File, itemName: String, formatter: IFormatter) {
        val file = File(root, itemName)
        isFolder = file.isDirectory
        date = formatter.formatDate(file.lastModified())
        size = if (isFolder) null else formatter.formatFileSize(file.length())

        if (isFolder) {
            name = itemName
            extension = null
        } else {
            val extensionIndex = FilenameUtils.indexOfExtension(itemName)
            if (extensionIndex == -1) {
                name = itemName
                extension = null
            } else {
                name = itemName.substring(0, extensionIndex)
                extension = itemName.substring(extensionIndex + 1)
            }
        }
    }
}

class FileSystemListResponseFactory(
        private val context: Context
) : IFormatter {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

    fun generate(root: File): List<FileSystemEntry> {
        val list = root.list()

        val result = ArrayList<FileSystemEntry>(list?.size ?: 0)

        list?.forEach {
            result += FileSystemEntry(root, it, this)
        }

        return result
    }

    override fun formatFileSize(size: Long) = Formatter.formatFileSize(context, size)

    override fun formatDate(timestamp: Long) = dateFormat.format(Date(timestamp))

}

class FileSystemCount(
        val totalEntries: Int
)
