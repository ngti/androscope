@file:Suppress("MemberVisibilityCanBePrivate")

package nl.ngti.androscope.responses

import android.content.Context
import android.text.format.Formatter
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

class Breadcrumb(
        val name: String,
        val path: String
)

class FileSystemEntry {
    val name: String
    val extension: String?
    val isFolder: Boolean
    val date: Date
    val size: String?

    constructor(context: Context, root: File, itemName: String) {
        val file = File(root, itemName)
        isFolder = file.isDirectory
        date = Date(file.lastModified())
        size = if (isFolder) null else Formatter.formatFileSize(context, file.length())

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

class FileSystemCount(
        val totalEntries: Int
)
