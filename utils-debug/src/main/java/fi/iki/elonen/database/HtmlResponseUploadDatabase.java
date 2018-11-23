package fi.iki.elonen.database;

import android.content.Context;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shows a form to upload the database.
 */
public class HtmlResponseUploadDatabase extends BaseDatabaseHtmlResponse {

    public HtmlResponseUploadDatabase(Context context, String databaseName) {
        super(context, databaseName);
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
        html.append("<p><div style='border: 1px solid #000000'><form action='/uploadDb' method='post' enctype='multipart/form-data'>\n" +
            "    <input type='file' name='file' />Upload Database here\n" +
            "    <input type='submit' name='submit' value='Upload'/>\n" +
            "</form></div></p>");
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        if (isUpload(session)) {
            NanoHTTPD.Method method = session.getMethod();
            if (NanoHTTPD.Method.POST.equals(method)) {
                try {
                    Map<String, String> files = new HashMap<>();
                    session.parseBody(files);

                    Set<String> keys = files.keySet();
                    for (String key : keys) {
                        String location = files.get(key);

                        File tempFile = new File(location);
                        File dbFile = mContext.getDatabasePath(mDatabaseName).getAbsoluteFile();
                        dbFile.delete();
                        tempFile.renameTo(dbFile);
                    }

                    return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Database uploaded! Restart application");

                } catch (Throwable e) {
                    System.out.println("i am error file upload post ");
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    private boolean isUpload(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/uploadDb");
    }

}
