package nl.ngti.androscope;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class AndroscopeActivity extends Activity {

    private TextView mInfoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_androscope);

        mInfoView = findViewById(R.id.text_androscope_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroscopeService.startServer(this, true, new ServiceCallback(this));
    }

    private static final class ServiceCallback extends ResultReceiver {

        private final WeakReference<AndroscopeActivity> mActivityRef;

        ServiceCallback(AndroscopeActivity activity) {
            super(new Handler(Looper.getMainLooper()));

            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            final AndroscopeActivity activity = mActivityRef.get();
            if (activity != null) {
                activity.mInfoView.setText(AndroscopeService.getResultMessage(resultData));
            }
        }
    }
}
