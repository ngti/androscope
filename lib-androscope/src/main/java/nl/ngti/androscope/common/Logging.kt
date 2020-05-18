package nl.ngti.androscope.common

import android.os.SystemClock
import android.util.Log
import nl.ngti.androscope.BuildConfig

internal inline fun <R> Any.logTime(block: () -> R, messageSupplier: () -> String): R {
    return if (BuildConfig.DEBUG) {
        val start = SystemClock.elapsedRealtime()
        try {
            block()
        } finally {
            val elapsed = SystemClock.elapsedRealtime() - start
            Log.d(javaClass.simpleName, messageSupplier() + ": %,d ms".format(elapsed))
        }
    } else {
        block()
    }
}

internal inline fun Any.log(messageSupplier: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d(javaClass.simpleName, messageSupplier())
    }
}
