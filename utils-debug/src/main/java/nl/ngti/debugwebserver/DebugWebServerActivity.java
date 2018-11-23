package nl.ngti.debugwebserver;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class DebugWebServerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugWebServerStartService.startServer(this, true);
        moveTaskToBack(true);
    }
}
