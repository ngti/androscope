package nl.ngti.androscope.service

import android.content.Context
import android.os.Build
import android.system.ErrnoException
import android.system.OsConstants
import nl.ngti.androscope.common.log
import nl.ngti.androscope.server.AndroscopeHttpServer
import nl.ngti.androscope.utils.AndroscopeMetadata
import java.io.IOException
import java.net.BindException

internal class AndroscopeServerHelper constructor(
        private val context: Context,
        private val callback: Callback
) {
    private var server: AndroscopeHttpServer? = null

    @Synchronized
    fun start() {
        server?.run {
            if (isAlive) {
                callback.onAlreadyRunning(this)
                return
            }
        }

        callback.onStarting()

        val metadata = AndroscopeMetadata.fromContext(context)
        val portRange = metadata.httpPortRange

        server = attemptToStartServer(portRange, metadata)
    }

    @Synchronized
    fun stop() {
        server?.stop()
        callback.onStopped()
    }

    private fun attemptToStartServer(
            portRange: IntRange,
            metadata: AndroscopeMetadata
    ): AndroscopeHttpServer? {

        var lastException: IOException? = null

        portRange.forEach { port ->
            log { "Trying port $port" }
            val server = AndroscopeHttpServer(context, port, metadata)

            try {
                server.start()
                callback.onStarted(server)
                return server
            } catch (e: IOException) {
                server.stop()

                lastException = e
                if (!shouldRetry(e)) {
                    callback.onError(e)
                    return null
                }
            }
        }
        callback.onError(
                IOException(
                        "Failed to start Androscope within port range $portRange.",
                        lastException)
        )
        return null
    }

    private fun shouldRetry(e: Throwable): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                e is ErrnoException) {
            return e.errno == OsConstants.EADDRINUSE
        }
        if (e is BindException) {
            return e.cause?.let { shouldRetry(it) } ?: false
        }
        return false
    }

    internal interface Callback {
        fun onStarting()
        fun onStarted(server: AndroscopeHttpServer)
        fun onAlreadyRunning(server: AndroscopeHttpServer)
        fun onError(e: IOException)
        fun onStopped()
    }
}
