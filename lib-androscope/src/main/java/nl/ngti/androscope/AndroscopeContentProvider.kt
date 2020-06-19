package nl.ngti.androscope

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import nl.ngti.androscope.service.AndroscopeService
import nl.ngti.androscope.utils.AndroscopeMetadata

internal class AndroscopeContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val context = context!!

        if (AndroscopeMetadata.isAutoStartEnabled(context)) {
            registerActivityMonitor(context)
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

    private fun registerActivityMonitor(context: Context) {
        (context.applicationContext as Application).run {
            registerActivityLifecycleCallbacks(AndroscopeStartCallback(this))
        }
    }
}

private class AndroscopeStartCallback(
        private val application: Application
) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        AndroscopeService.startServer(activity)

        application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}
