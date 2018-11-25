package fi.iki.elonen.responses.filebrowser;

import android.content.Context;
import fi.iki.elonen.HttpResponse;
import java.util.Map;

public class FileResponseFactory {

    public static HttpResponse from(Context context, Map<String, String> parms) {
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
