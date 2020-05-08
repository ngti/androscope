package nl.ngti.androscope.common

import android.util.Log
import nl.ngti.androscope.BuildConfig
import java.lang.Exception

@Suppress("NOTHING_TO_INLINE")
internal inline fun Any.log(message: String, vararg params: Any) {
    if (BuildConfig.DEBUG) {
        Log.d(javaClass.simpleName, message.format(*params))
    }
}

internal inline fun Any.log(messageSupplier: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d(javaClass.simpleName, messageSupplier())
    }
}

internal inline fun Any.log(exception: Throwable, messageSupplier: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.e(javaClass.simpleName, messageSupplier(), exception)
    }
}
