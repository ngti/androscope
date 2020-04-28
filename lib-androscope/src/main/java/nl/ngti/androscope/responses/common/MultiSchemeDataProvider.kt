package nl.ngti.androscope.responses.common

import android.net.Uri

class MultiSchemeDataProvider : UriDataProvider {

    private val schemeToProviderMap = HashMap<String, UriDataProvider>()

    fun addProvider(scheme: String, provider: UriDataProvider) {
        schemeToProviderMap[scheme]?.let {
            throw IllegalStateException("Attempt to overwrite scheme $scheme, current value = $it")
        }
        schemeToProviderMap[scheme] = provider
    }

    override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
    ) = schemeToProviderMap[uri.scheme]?.run {
        query(uri, projection, selection, selectionArgs, sortOrder)
    } ?: throw IllegalArgumentException("No provider to handle ${uri.scheme}")
}
