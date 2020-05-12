package nl.ngti.androscope.responses.files

import android.content.Context
import android.text.format.Formatter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class FileSystemEntryListFactory(
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
