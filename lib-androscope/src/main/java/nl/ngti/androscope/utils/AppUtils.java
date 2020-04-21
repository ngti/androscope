package nl.ngti.androscope.utils;

import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;

public final class AppUtils {

    private AppUtils() {
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
