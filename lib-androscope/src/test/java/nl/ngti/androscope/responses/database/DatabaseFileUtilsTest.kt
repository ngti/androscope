package nl.ngti.androscope.responses.database

import junit.framework.TestCase.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DatabaseFileUtilsTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    @Test
    fun `return values in normal setup`() {
        // Simulate database with auxiliary files
        val dbFile = createTestFile("test.db")
        val journalFile = createTestFile("test.db-journal")
        val shmFile = createTestFile("test.db-shm")
        val walFile = createTestFile("test.db-wal")
        val otherFile = createTestFile("test.db-wal1")

        assertEquals(dbFile, dbFile.mainDatabaseFile)
        assertEquals(dbFile, journalFile.mainDatabaseFile)
        assertEquals(dbFile, shmFile.mainDatabaseFile)
        assertEquals(dbFile, walFile.mainDatabaseFile)
        assertEquals(otherFile, otherFile.mainDatabaseFile)

        assertFalse(dbFile.isAuxiliaryDatabaseFile)
        assertTrue(journalFile.isAuxiliaryDatabaseFile)
        assertTrue(shmFile.isAuxiliaryDatabaseFile)
        assertTrue(walFile.isAuxiliaryDatabaseFile)
        assertFalse(otherFile.isAuxiliaryDatabaseFile)

        dbFile.collectAllDatabaseFiles().apply {
            assertEquals(dbFile.parentFile, parentDirectory)
            assertEquals(4, size)
            assertTrue(containsFile(dbFile.name))
            assertTrue(containsFile(journalFile.name))
            assertTrue(containsFile(shmFile.name))
            assertTrue(containsFile(walFile.name))
        }
    }

    @Test
    fun `when database name has auxiliary suffix`() {
        // Database file name contains a suffix of an auxiliary file, but there is no other
        // database file named "test", so it must be considered as a main database file.
        val dbFile = createTestFile("test-journal")
        val journalFile = createTestFile("test-journal-journal")
        val shmFile = createTestFile("test-journal-shm")
        val walFile = createTestFile("test-journal-wal")

        assertEquals(dbFile, dbFile.mainDatabaseFile)
        assertFalse(dbFile.isAuxiliaryDatabaseFile)

        dbFile.collectAllDatabaseFiles().run {
            assertEquals(dbFile.parentFile, parentDirectory)
            assertEquals(4, size)
            assertTrue(containsFile(dbFile.name))
            assertTrue(containsFile(journalFile.name))
            assertTrue(containsFile(shmFile.name))
            assertTrue(containsFile(walFile.name))
        }
    }

    private fun createTestFile(name: String): File = tempFolder.newFile(name)
}
