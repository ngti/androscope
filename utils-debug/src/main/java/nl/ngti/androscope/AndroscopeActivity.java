package nl.ngti.androscope;

import android.app.Activity;

public class AndroscopeActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        AndroscopeService.startServer(this, true);
    }
}
