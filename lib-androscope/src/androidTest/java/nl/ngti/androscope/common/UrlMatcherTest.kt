package nl.ngti.androscope.common

import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class UrlMatcherTest {

    private val urlMatcher = UrlMatcher<TestUrlHandler>()

    @Test(expected = IllegalArgumentException::class)
    fun addEmptyRootPath() {
        urlMatcher.add("", null, RestUrlHandler())
    }

    @Test
    fun get() {
        val restUrlHandler = RestUrlHandler()
        val downloadUrlHandler = DownloadUrlHandler()
        val viewUrlHandler = ViewUrlHandler()
        val matchAllRootPath = MatchHandler()
        urlMatcher.add("rest", "provider/data", restUrlHandler)
        urlMatcher.add("rest", "provider/metadata", restUrlHandler)
        urlMatcher.add("rest", "file-system/list", restUrlHandler)
        urlMatcher.add("rest", "file-system/breadcrumbs", restUrlHandler)
        urlMatcher.add("download", null, downloadUrlHandler)
        urlMatcher.add("view", null, viewUrlHandler)
        urlMatcher.add("*", null, matchAllRootPath)

        assertSame(restUrlHandler, urlMatcher["http://rest/provider/data"])
        assertSame(restUrlHandler, urlMatcher["http://rest/provider/metadata"])
        assertSame(restUrlHandler, urlMatcher["http://rest/file-system/list"])
        assertSame(restUrlHandler, urlMatcher["http://rest/file-system/breadcrumbs"])
        assertSame(downloadUrlHandler, urlMatcher["http://download"])
        assertSame(viewUrlHandler, urlMatcher["http://view"])
        assertSame(matchAllRootPath, urlMatcher["http://match_all"])

        assertNull(urlMatcher["http://not/existing"])
    }
}

private open class TestUrlHandler

private class RestUrlHandler : TestUrlHandler()
private class DownloadUrlHandler : TestUrlHandler()
private class ViewUrlHandler : TestUrlHandler()
private class MatchHandler : TestUrlHandler()
