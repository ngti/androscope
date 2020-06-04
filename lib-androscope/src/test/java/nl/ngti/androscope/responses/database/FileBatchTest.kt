package nl.ngti.androscope.responses.database

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FileBatchTest {

    private val testFilesCount = 5
    private val middleIndex = 2

    private val fileNames = List(testFilesCount) {
        "file$it"
    }

    @Rule
    @JvmField
    val sourceFolder = TemporaryFolder()

    @Rule
    @JvmField
    val destinationFolder = TemporaryFolder()

    private val originalFiles = ArrayList<File>(fileNames.size)

    private lateinit var destination: File

    private lateinit var movedFiles: List<File>

    private lateinit var batch: FileBatch

    @Before
    fun setUp() {
        batch = FileBatch(sourceFolder.root)

        fileNames.forEach {
            originalFiles += sourceFolder.newFile(it)
            batch += it
        }

        destination = destinationFolder.root

        movedFiles = fileNames.map {
            File(destination, it)
        }
    }

    @Test
    fun `moveTo success`() {
        val result = batch.moveTo(destination)
                ?: throw AssertionError("Moving batch failed")

        assertEquals(destination, result.parentDirectory)
        assertEquals(fileNames.size, result.size)

        fileNames.forEach {
            assertTrue(result.containsFile(it))
        }

        originalFiles.assertExist(false)
        movedFiles.assertExist()
    }

    @Test
    fun `moveTo restore on failure`() {
        // Add non-existing file in the end, so batch will fail
        batch += "non_existing_file"

        assertNull(batch.moveTo(destination, revertIfFailed = true))

        originalFiles.assertExist()
        movedFiles.assertExist(false)

        assertNull(batch.moveTo(destination, revertIfFailed = false))

        originalFiles.assertExist(false)
        movedFiles.assertExist()
    }

    @Test
    fun `moveTo stops on failure`() {
        assert(originalFiles[middleIndex].delete())

        assertNull(batch.moveTo(destination, revertIfFailed = true))

        originalFiles
                .filterIndexed { index, _ ->
                    index != middleIndex
                }
                .assertExist()
        assertFalse(originalFiles[middleIndex].exists())
        assertFalse(movedFiles[middleIndex].exists())
        movedFiles.assertExist(false)

        assertNull(batch.moveTo(destination, revertIfFailed = false))

        originalFiles
                .filterIndexed { index, _ ->
                    index > middleIndex
                }
                .assertExist()
        assertFalse(originalFiles[middleIndex].exists())
        assertFalse(movedFiles[middleIndex].exists())
        movedFiles
                .filterIndexed { index, _ ->
                    index < middleIndex
                }
                .assertExist()
    }

    private fun List<File>.assertExist(expected: Boolean = true) =
            forEach {
                assertEquals(expected, it.exists())
            }
}
