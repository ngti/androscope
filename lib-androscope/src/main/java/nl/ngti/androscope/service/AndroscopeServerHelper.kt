package nl.ngti.androscope.service

import android.content.Context
import nl.ngti.androscope.server.AndroscopeHttpServer
import java.io.IOException

internal class AndroscopeServerHelper private constructor(
        private val server: AndroscopeHttpServer,
        private val callback: Callback
) {
    fun start() {
        if (server.isAlive) {
            callback.onAlreadyRunning(server)
            return
        }
        callback.onStarting()
        try {
            server.start()
            callback.onStarted(server)
        } catch (e: IOException) {
            callback.onError(e)
        }
    }

    fun stop() {
        server.stop()
        callback.onStopped()
    }

    companion object {

        fun newInstance(context: Context, callback: Callback): AndroscopeServerHelper {
            val server = AndroscopeHttpServer.newInstance(context)
            return AndroscopeServerHelper(server, callback)
        }
    }

    internal interface Callback {
        fun onStarting()
        fun onStarted(server: AndroscopeHttpServer)
        fun onAlreadyRunning(server: AndroscopeHttpServer)
        fun onError(e: IOException)
        fun onStopped()
    }
}
