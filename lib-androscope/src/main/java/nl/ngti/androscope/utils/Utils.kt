package nl.ngti.androscope.utils

import android.content.Context

internal val Context.applicationName: CharSequence
    get() = packageManager.getApplicationLabel(applicationInfo)
