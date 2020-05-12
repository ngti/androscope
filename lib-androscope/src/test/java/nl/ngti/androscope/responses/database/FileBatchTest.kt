package nl.ngti.androscope.responses.database

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class FileBatchTest : BaseFileSystemTest() {

    private val testFilesCount = 5
    private val middleIndex = 2

    private val fileNames = List(testFilesCount) {
        "file$it"
    }

    private val batch = FileBatch(testDirectory)

    private val newDirectory = createTempDir(directory = testDirectory)

    private val originalFiles
        get() = fileNames.map {
            File(testDirectory, it)
        }

    private val movedFiles
        get() = fileNames.map {
            File(newDirectory, it)
        }

    init {
        fileNames.forEach {
            createTestFile(it)
            batch += it
        }
    }

    @Test
    fun moveToSuccess() {
        val result = batch.moveTo(newDirectory) ?: throw AssertionError("Moving batch failed")

        assertEquals(newDirectory, result.parentDirectory)
        assertEquals(fileNames.size, result.size)

        fileNames.forEach {
            assertTrue(result.containsFile(it))
        }

        assertFiles(originalFiles, exist = false)
        assertFiles(movedFiles, exist = true)
    }

    @Test
    fun moveTo_RestoreOnFailure() {
        // Add non-existing file in the end, so batch will fail
        batch += "non_existing_file"

        assertNull(batch.moveTo(newDirectory, revertIfFailed = true))

        assertFiles(originalFiles, exist = true)
        assertFiles(movedFiles, exist = false)

        assertNull(batch.moveTo(newDirectory, revertIfFailed = false))

        assertFiles(originalFiles, exist = false)
        assertFiles(movedFiles, exist = true)
    }

    @Test
    fun batchStopsMovingOnFailure() {
        assert(originalFiles[middleIndex].delete())

        assertNull(batch.moveTo(newDirectory, revertIfFailed = true))

        assertFiles(
                originalFiles.filterIndexed { index, _ ->
                    index != middleIndex
                },
                exist = true

        )
        assertFalse(originalFiles[middleIndex].exists())
        assertFalse(movedFiles[middleIndex].exists())
        assertFiles(movedFiles, exist = false)

        assertNull(batch.moveTo(newDirectory, revertIfFailed = false))

        assertFiles(
                originalFiles.filterIndexed { index, _ ->
                    index > middleIndex
                },
                exist = true
        )
        assertFalse(originalFiles[middleIndex].exists())
        assertFalse(movedFiles[middleIndex].exists())
        assertFiles(
                movedFiles.filterIndexed { index, _ ->
                    index < middleIndex
                },
                exist = true
        )
    }

    private fun assertFiles(files: List<File>, exist: Boolean) {
        files.forEach {
            assertEquals(exist, it.exists())
        }
    }
}
