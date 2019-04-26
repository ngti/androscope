package nl.ngti.androscope.server;

import androidx.annotation.NonNull;

import java.util.Arrays;

import fi.iki.elonen.NanoHTTPD;

public final class SessionWrapper {

    private static final String PATH_SEPARATOR = "/";

    private static final String EMPTY_STRING = "";

    private final NanoHTTPD.IHTTPSession mIHTTPSession;

    private String[] mSplitPaths;

    public SessionWrapper(NanoHTTPD.IHTTPSession ihttpSession) {
        mIHTTPSession = ihttpSession;
    }

    @NonNull
    public String getRootPath() {
        splitPaths();
        return mSplitPaths[0];
    }

    @NonNull
    public String getSecondaryPath() {
        splitPaths();

        if (mSplitPaths.length <= 1) {
            return EMPTY_STRING;
        }

        return mSplitPaths[1];
    }

    private void splitPaths() {
        if (mSplitPaths != null) {
            return;
        }
        final String uri = mIHTTPSession.getUri();

        if (PATH_SEPARATOR.equals(uri)) {
            mSplitPaths = new String[]{EMPTY_STRING};
        } else {
            final String[] split = uri.split(PATH_SEPARATOR);

            mSplitPaths = Arrays.copyOfRange(split, 1, split.length);
        }
        //Log.d("Test", "Paths split: " + Arrays.toString(mSplitPaths));
    }
}
