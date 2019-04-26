package nl.ngti.androscope.responses.database;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import nl.ngti.androscope.responses.HttpResponse;

abstract class BaseDatabaseHtmlResponse implements HttpResponse {

    private static final String KEY_DATABASE_NAME = "nl.ngti.androscope.DATABASE_NAME";

    protected final Context mContext;
    protected String mDatabaseName;

    BaseDatabaseHtmlResponse(Context context) {
        mContext = context;
    }

    @Override
    public boolean isEnabled(Bundle metadata) {
        mDatabaseName = metadata.getString(KEY_DATABASE_NAME);
        boolean enabled = !TextUtils.isEmpty(mDatabaseName);
        if (!enabled) {
            Log.w("androscope", KEY_DATABASE_NAME + " metadata was not specified. Not showing database-related items");
        }
        return enabled;
    }
}
