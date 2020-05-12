package nl.ngti.androscope.responses.database

import androidx.annotation.VisibleForTesting
import java.io.File

internal class FileBatch(
        val parentDirectory: File
) {

    private val fileNames = ArrayList<String>()

    operator fun plusAssign(fileName: String) {
        fileNames += fileName
    }

    fun moveTo(newParentDirectory: File, revertIfFailed: Boolean = true): FileBatch? {
        val renamed = FileBatch(newParentDirectory)

        var result = true
        for (fileName in fileNames) {
            val sourceFile = File(parentDirectory, fileName)
            val destFile = File(newParentDirectory, fileName)

            if (sourceFile.renameTo(destFile)) {
                renamed += fileName
            } else {
                result = false
                break
            }
        }

        if (!result) {
            if (revertIfFailed) {
                // Attempt to restore original files as much as we can, but avoid recursive calls
                renamed.moveTo(parentDirectory, revertIfFailed = false)
            }
            return null
        }

        return renamed
    }

    @VisibleForTesting
    val size
        get() = fileNames.size

    @VisibleForTesting
    fun containsFile(fileName: String) = fileNames.contains(fileName)
}
