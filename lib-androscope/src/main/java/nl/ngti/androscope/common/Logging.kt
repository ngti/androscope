package nl.ngti.androscope.common

import android.util.Log
import nl.ngti.androscope.BuildConfig

@Suppress("NOTHING_TO_INLINE")
inline fun Any.log(message: String, vararg params: Any) {
    if (BuildConfig.DEBUG) {
        Log.d(javaClass.simpleName, message.format(*params))
    }
}

inline fun Any.log(messageSupplier: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d(javaClass.simpleName, messageSupplier())
    }
}
