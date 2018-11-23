package fi.iki.elonen;

import android.content.Context;

abstract class BaseDatabaseHtmlResponse implements HtmlResponse {

    protected final Context mContext;
    protected final String mDatabaseName;

    BaseDatabaseHtmlResponse(Context context, String databaseName) {
        mContext = context;
        mDatabaseName = databaseName;
    }

}
