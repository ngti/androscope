package nl.ngti.androscope.responses.database

import junit.framework.TestCase.*
import org.junit.Test

class DatabaseFileUtilsTest : BaseFileSystemTest() {

    @Test
    fun `return values in normal setup`() {
        // Simulate database with auxiliary files
        val dbFile = createTestFile("test.db")
        val journalFile = createTestFile("test.db-journal")
        val shmFile = createTestFile("test.db-shm")
        val walFile = createTestFile("test.db-wal")
        val otherFile = createTestFile("test.db-wal1")

        assertEquals(dbFile, getMainDatabaseFile(dbFile))
        assertEquals(dbFile, getMainDatabaseFile(journalFile))
        assertEquals(dbFile, getMainDatabaseFile(shmFile))
        assertEquals(dbFile, getMainDatabaseFile(walFile))
        assertEquals(otherFile, getMainDatabaseFile(otherFile))

        assertFalse(isAuxiliaryDatabaseFile(dbFile))
        assertTrue(isAuxiliaryDatabaseFile(journalFile))
        assertTrue(isAuxiliaryDatabaseFile(shmFile))
        assertTrue(isAuxiliaryDatabaseFile(walFile))
        assertFalse(isAuxiliaryDatabaseFile(otherFile))

        collectAllDatabaseFiles(dbFile).apply {
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

        assertEquals(dbFile, getMainDatabaseFile(dbFile))
        assertFalse(isAuxiliaryDatabaseFile(dbFile))

        collectAllDatabaseFiles(dbFile).apply {
            assertEquals(dbFile.parentFile, parentDirectory)
            assertEquals(4, size)
            assertTrue(containsFile(dbFile.name))
            assertTrue(containsFile(journalFile.name))
            assertTrue(containsFile(shmFile.name))
            assertTrue(containsFile(walFile.name))
        }
    }
}
