package fi.iki.elonen.database;

import android.content.Context;
import fi.iki.elonen.HtmlResponse;

abstract class BaseDatabaseHtmlResponse implements HtmlResponse {

    protected final Context mContext;
    protected final String mDatabaseName;

    BaseDatabaseHtmlResponse(Context context, String databaseName) {
        mContext = context;
        mDatabaseName = databaseName;
    }

}
