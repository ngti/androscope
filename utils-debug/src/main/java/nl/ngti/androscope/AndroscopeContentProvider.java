package nl.ngti.androscope;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import nl.ngti.androscope.server.AndroscopeHttpServer;
import nl.ngti.androscope.service.AndroscopeService;
import nl.ngti.androscope.utils.AppUtils;

public final class AndroscopeContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        //noinspection ConstantConditions
        @NonNull final Context context = getContext();

        final Bundle metadata = AppUtils.getMetadata(context);
        if (metadata.getBoolean(AndroscopeHttpServer.KEY_AUTO_START)) {
            AndroscopeService.startServer(context);
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
