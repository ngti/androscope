package fi.iki.elonen.filebrowser;

import android.content.Context;
import fi.iki.elonen.HtmlResponse;
import java.util.Map;

public class FileResponseFactory {

    public static HtmlResponse from(Context context, Map<String, String> parms) {
        if (isView(parms)) {
            return new HtmlResponseViewFile(context);
        } else if (isDownload(parms)) {
            return new HtmlResponseDownloadFile();
        } else {
            return new HtmlResponseShowFolder(context);
        }
    }

    private static boolean isDownload(Map<String, String> parms) {
        return parms.containsKey("download");
    }

    private static boolean isView(Map<String, String> parms) {
        return parms.containsKey("view");
    }
}
