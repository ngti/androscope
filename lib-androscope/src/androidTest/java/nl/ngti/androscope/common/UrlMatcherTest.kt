package nl.ngti.androscope.common

import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class UrlMatcherTest {

    private val urlMatcher = UrlMatcher<TestUrlHandler>()

    @Test(expected = IllegalArgumentException::class)
    fun buildEmptyRootPath() {
        urlMatcher.build("") {}
    }

    @Test(expected = IllegalArgumentException::class)
    fun buildRootPathWithSubPaths() {
        urlMatcher.build("sub/path") {}
    }

    @Test(expected = IllegalArgumentException::class)
    fun builderAddPathWithSubPaths() {
        urlMatcher.build("root") {
            add("sub/path", TestUrlHandler())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun builderAddSubPathWithSubPaths() {
        urlMatcher.build("root") {
            addSubPath("sub/path") { }
        }
    }

    @Test
    fun get() {
        val restUrlHandler1 = RestUrlHandler()
        val restUrlHandler2 = RestUrlHandler()
        val fileSystemHandler1 = FileSystemHandler()
        val fileSystemHandler2 = FileSystemHandler()
        val downloadUrlHandler = DownloadUrlHandler()
        val viewUrlHandler = ViewUrlHandler()
        val otherHandler = TestUrlHandler()

        urlMatcher.build("rest") {
            addSubPath("provider") {
                add("data", restUrlHandler1)
                add("metadata", restUrlHandler2)
            }
            addSubPath("file-system") {
                add("list", fileSystemHandler1)
                add("breadcrumbs", fileSystemHandler2)
            }

            add("download", downloadUrlHandler)
            add("view", viewUrlHandler)
        }

        urlMatcher.build("root") {
            addSubPath("very") {
                addSubPath("deep") {
                    addSubPath("sub") {
                        add("path", otherHandler)
                    }
                }
            }
        }

        assertSame(restUrlHandler1, urlMatcher["http://rest/provider/data"])
        assertSame(restUrlHandler2, urlMatcher["http://rest/provider/metadata"])
        assertSame(fileSystemHandler1, urlMatcher["http://rest/file-system/list"])
        assertSame(fileSystemHandler2, urlMatcher["http://rest/file-system/breadcrumbs"])
        assertSame(downloadUrlHandler, urlMatcher["http://rest/download"])
        assertSame(viewUrlHandler, urlMatcher["http://rest/view"])
        assertSame(otherHandler, urlMatcher["http://root/very/deep/sub/path"])

        assertNull(urlMatcher["http://not/existing"])
    }
}

private open class TestUrlHandler

private class RestUrlHandler : TestUrlHandler()
private class FileSystemHandler : TestUrlHandler()
private class DownloadUrlHandler : TestUrlHandler()
private class ViewUrlHandler : TestUrlHandler()
