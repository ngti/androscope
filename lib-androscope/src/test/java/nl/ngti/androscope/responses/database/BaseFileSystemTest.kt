package nl.ngti.androscope.responses.database

import org.junit.After
import java.io.File

abstract class BaseFileSystemTest {

    protected val testDirectory by lazy {
        val userDirectory = File(System.getProperty("user.dir")!!)
        createTempDir(directory = userDirectory).also {
            println("${javaClass.simpleName} test directory: $it")
        }
    }

    @After
    fun tearDown() {
        val deleted = testDirectory.deleteRecursively()
        println("${javaClass.simpleName} test directory ($testDirectory) deleted: $deleted")
    }

    protected fun createTestFile(name: String) = File(testDirectory, name).apply {
        assert(createNewFile()) {
            "Failed to create $absolutePath"
        }
    }
}
