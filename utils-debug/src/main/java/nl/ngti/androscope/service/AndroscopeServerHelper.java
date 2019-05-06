package nl.ngti.androscope.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import nl.ngti.androscope.server.AndroscopeHttpServer;

final class AndroscopeServerHelper {

    private final AndroscopeHttpServer mServer;
    private final Callback mCallback;

    private AndroscopeServerHelper(@NonNull AndroscopeHttpServer server,
                                   @NonNull Callback callback) {

        mServer = server;
        mCallback = callback;
    }

    @NonNull
    static AndroscopeServerHelper newInstance(@NonNull Context context,
                                              @NonNull Callback callback) {

        final AndroscopeHttpServer server = AndroscopeHttpServer.newInstance(context);
        return new AndroscopeServerHelper(server, callback);
    }

    void start() {
        if (mServer.isAlive()) {
            mCallback.onAlreadyRunning(mServer);
            return;
        }
        mCallback.onStarting();
        try {
            mServer.start();
            mCallback.onStarted(mServer);
        } catch (IOException e) {
            mCallback.onError(e);
        }
    }

    void stop() {
        mServer.stop();

        mCallback.onStopped();
    }

    interface Callback {

        void onStarting();

        void onStarted(AndroscopeHttpServer server);

        void onAlreadyRunning(AndroscopeHttpServer server);

        void onError(IOException e);

        void onStopped();
    }
}
