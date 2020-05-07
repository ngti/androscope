package nl.ngti.androscope.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle

class AndroscopeMetadata private constructor(
        private val internal: Bundle
) {

    val httpPort: Int
        get() = internal.getInt(KEY_HTTP_PORT, HTTP_PORT)

    val databaseName: String?
        get() = internal.getString(KEY_DATABASE_NAME)

    val imageCacheConfig: ImageCacheConfig?
        get() {
            val cache = internal.getString(KEY_IMAGE_CACHE)
            val filter = internal.getString(KEY_IMAGE_CACHE_FILTER)
            if (cache?.isNotBlank() == true && filter?.isNotBlank() == true) {
                return ImageCacheConfig("Configured in manifest", cache, filter)
            }
            return null
        }

    companion object {

        private const val KEY_AUTO_START = "nl.ngti.androscope.AUTO_START"

        private const val KEY_HTTP_PORT = "nl.ngti.androscope.HTTP_PORT"
        private const val HTTP_PORT = 8787

        private const val KEY_DATABASE_NAME = "nl.ngti.androscope.DATABASE_NAME"

        private const val KEY_IMAGE_CACHE = "nl.ngti.androscope.IMAGE_CACHE"
        private const val KEY_IMAGE_CACHE_FILTER = "nl.ngti.androscope.IMAGE_CACHE.filter"

        @JvmStatic
        fun fromContext(context: Context): AndroscopeMetadata {
            return AndroscopeMetadata(getManifestMetadata(context)!!)
        }

        fun isAutoStartEnabled(context: Context): Boolean =
                getManifestMetadata(context)?.getBoolean(KEY_AUTO_START) ?: false

        private fun getManifestMetadata(context: Context): Bundle? {
            return try {
                context.packageManager.getApplicationInfo(
                        context.packageName, PackageManager.GET_META_DATA
                ).metaData
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }
}
