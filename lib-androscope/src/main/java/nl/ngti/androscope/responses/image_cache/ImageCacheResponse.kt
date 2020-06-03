package nl.ngti.androscope.responses.image_cache

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.common.Refreshable
import nl.ngti.androscope.responses.common.ResponseDataCache
import nl.ngti.androscope.server.*
import nl.ngti.androscope.utils.AndroscopeMetadata
import nl.ngti.androscope.utils.ImageCacheConfig
import nl.ngti.androscope.utils.defaultConfigs
import java.io.File
import java.io.FileInputStream
import java.util.regex.Pattern
import kotlin.math.min

private const val MANIFEST_CONFIG_TYPE = "manifest"

internal class ImageCacheResponse(
        private val context: Context,
        private val metadata: AndroscopeMetadata
) {
    private val cache = ResponseDataCache(
            paramsSupplier = {
                val config = it.imageCacheConfig
                ImageCacheParams(config, File(rootCacheDir, config.path), it.timestamp)
            },
            dataSupplier = {
                ImageCacheCachedData(it)
            }
    )

    private val rootCacheDir get() = context.cacheDir

    fun getList(): List<ImageCache> {
        val result = ArrayList<ImageCache>()

        metadata.imageCacheConfig?.run {
            result += ImageCache(MANIFEST_CONFIG_TYPE, title)
        }

        defaultConfigs.forEach { (name, config) ->
            val imageCacheDir = File(rootCacheDir, config.path)
            if (imageCacheDir.exists()) {
                result += ImageCache(name, config.title)
            }
        }

        return result
    }

    fun getInfo(sessionParams: SessionParams): ImageCacheInfo {
        return try {
            val cachedData = cache[sessionParams]
            ImageCacheInfo(totalEntries = cachedData.count)
        } catch (e: Throwable) {
            ImageCacheInfo(error = e.message)
        }
    }

    fun getData(sessionParams: SessionParams) = cache[sessionParams].getList(sessionParams)

    fun getThumbnail(sessionParams: SessionParams): NanoHTTPD.Response? {
        val cachedData = cache[sessionParams]
        val fileName = sessionParams.fileName
        val file = cachedData[fileName]

        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK,
                "image/jpeg",
                FileInputStream(file)
        ).apply {
            addHeader("Content-Disposition", "filename=\"$fileName\"")
        }
    }

    private val SessionParams.imageCacheConfig: ImageCacheConfig
        get() = when (val type = this["type"]) {
            MANIFEST_CONFIG_TYPE -> metadata.imageCacheConfig
                    ?: throw IllegalStateException("Manifest config is not available")
            else -> {
                defaultConfigs[type]
                        ?: throw IllegalStateException("Unsupported config type: $type")
            }
        }

    private val SessionParams.fileName: String
        get() = this["file"]!!
}

private data class ImageCacheParams(
        val config: ImageCacheConfig,
        val root: File,
        override val timestamp: Long
) : Refreshable

private class ImageCacheCachedData(
        private val params: ImageCacheParams
) {

    private val root get() = params.root
    private val list: List<ImageCacheEntry>

    init {
        require(root.exists()) { "$root does not exist." }
        require(root.isDirectory) { "$root is not a directory." }
        val fileList = root.list() ?: emptyArray()

        val pattern = Pattern.compile(params.config.filter)
        val output = ArrayList<ImageCacheEntry>(fileList.size)

        fileList.forEach {
            if (pattern.matcher(it).matches()) {
                val file = File(root, it)
                output += ImageCacheEntry(it, file.length())
            }
        }

        output.trimToSize()

        list = output
    }

    val count get() = list.size

    operator fun get(fileName: String) = File(root, fileName)

    fun getList(session: SessionParams): List<ImageCacheEntry> {
        val pageSize = session.pageSize
        val pageNumber = session.pageNumber

        val fromIndex = pageSize * pageNumber
        val toIndex = min(fromIndex + pageSize, count)

        return list.subList(fromIndex, toIndex)
    }

}
