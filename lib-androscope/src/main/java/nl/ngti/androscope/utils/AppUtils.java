package nl.ngti.androscope.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;

public final class AppUtils {

    private AppUtils() {
    }

    @NonNull
    public static Bundle getMetadata(@NonNull final Context context) {
        try {
            final ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static String getMimeType(@NonNull String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (mimeType == null) {
            if ("js".equals(extension)) {
                mimeType = "application/javascript";
            }
        }

        return mimeType;
    }
}
