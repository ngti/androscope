package fi.iki.elonen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;
import static fi.iki.elonen.filebrowser.HtmlResponseViewFile.getMimeType;

/**
 * Shows a file explorer to access quickly the private file storage of the app.
 */
public class HtmlResponseThumbnail implements HtmlResponse {

    private final Context mContext;

    public HtmlResponseThumbnail(Context context) {
        mContext = context;
    }

    @Override
    public void showHtmlHeader(NanoHTTPD.IHTTPSession session, StringBuilder html) {
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        if (isProcessable(session)) {
            String viewPath = session.getParms().get("file");
            File file = new File(viewPath);
            String mime = getMimeType(mContext, session.getParms(), file);
            try {
                Bitmap bmp = null;
                if (mime.startsWith("image/")) {
                    bmp = resizeBitmapFile(128, file);
                } else if (mime.startsWith("video/")) {
                    bmp = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MICRO_KIND);
                }
                if (bmp != null) {
                    File tempFile = File.createTempFile("_thumbnail_", "");
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(tempFile));
                    NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "image/jpeg", new FileInputStream(tempFile));
                    response.addHeader("Content-Disposition", "filename=\"" + file.getName() + "\"");
                    return response;
                }
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean isProcessable(NanoHTTPD.IHTTPSession session) {
        return session.getUri().startsWith("/thumbnail");
    }

    private Bitmap resizeBitmapFile(
        int maxWidth,
        File file
    ) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int imageWidth = options.outWidth;

        BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inScaled = true;
        mBitmapOptions.inSampleSize = 4;
        mBitmapOptions.inDensity = imageWidth;
        mBitmapOptions.inTargetDensity = maxWidth * mBitmapOptions.inSampleSize;

        // will load & resize the image to be 1/inSampleSize dimensions
        return BitmapFactory.decodeFile(file.getAbsolutePath(), mBitmapOptions);
    }


}
