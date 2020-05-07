package nl.ngti.androscope.utils

data class ImageCacheConfig(
        val title: String,
        val path: String,
        val filter: String
)

val defaultConfigs
    get() =
        HashMap<String, ImageCacheConfig>(2).apply {
            put(
                    "picasso",
                    ImageCacheConfig(
                            "Picasso",
                            "picasso-cache",
                            "^.*\\.1$"
                    )
            )
            put(
                    "glide",
                    ImageCacheConfig(
                            "Glide",
                            "image_manager_disk_cache",
                            "^.*\\.0$"
                    )
            )
            put(
                    "coil",
                    ImageCacheConfig(
                            "Coil",
                            "image_cache",
                            "^.*\\.1$"
                    )
            )
        }
