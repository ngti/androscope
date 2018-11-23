package nl.ngti.debugwebserver;

import android.app.Activity;

public class DebugWebServerActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        DebugWebServerStartService.startServer(this, true);
    }
}
