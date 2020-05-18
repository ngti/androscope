package nl.ngti.androscope.utils

import android.content.Context

val Context.applicationName: CharSequence
    get() = packageManager.getApplicationLabel(applicationInfo)
