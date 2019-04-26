package nl.ngti.androscope.service;

import androidx.annotation.NonNull;

public final class AndroscopeServiceStatus {

    private static final int STATUS_OK = 0;
    private static final int STATUS_FAILED = 1;
    private static final int STATUS_STOPPED = 2;

    private final String mMessage;
    private final int mStatus;

    private AndroscopeServiceStatus(String message, int status) {
        mMessage = message;
        mStatus = status;
    }

    @NonNull
    static AndroscopeServiceStatus success(String message) {
        return new AndroscopeServiceStatus(message, STATUS_OK);
    }

    @NonNull
    static AndroscopeServiceStatus error(String message) {
        return new AndroscopeServiceStatus(message, STATUS_FAILED);
    }

    @NonNull
    static AndroscopeServiceStatus stopped(String message) {
        return new AndroscopeServiceStatus(message, STATUS_STOPPED);
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isRestartNeeded() {
        return mStatus != STATUS_OK;
    }
}
