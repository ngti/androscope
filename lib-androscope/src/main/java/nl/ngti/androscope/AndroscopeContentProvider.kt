package nl.ngti.androscope

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import nl.ngti.androscope.server.AndroscopeHttpServer
import nl.ngti.androscope.service.AndroscopeService
import nl.ngti.androscope.utils.AppUtils

class AndroscopeContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val context = context!!
        AppUtils.getMetadata(context)?.run {
            if (getBoolean(AndroscopeHttpServer.KEY_AUTO_START)) {
                AndroscopeService.startServer(context)
            }
        }
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?) =
            throw UnsupportedOperationException()

    override fun getType(uri: Uri) = throw UnsupportedOperationException()

    override fun insert(uri: Uri, values: ContentValues?) = throw UnsupportedOperationException()

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) =
            throw UnsupportedOperationException()

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?) =
            throw UnsupportedOperationException()
}
