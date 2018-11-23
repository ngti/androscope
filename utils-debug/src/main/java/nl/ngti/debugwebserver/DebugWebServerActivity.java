package nl.ngti.debugwebserver;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

public class DebugWebServerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugWebServerStartService.startServer(this, true);
    }
}
