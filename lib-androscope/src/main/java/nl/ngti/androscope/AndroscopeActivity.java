package nl.ngti.androscope;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import nl.ngti.androscope.service.AndroscopeService;
import nl.ngti.androscope.service.AndroscopeServiceStatus;

public final class AndroscopeActivity extends FragmentActivity
        implements ServiceConnection, Observer<AndroscopeServiceStatus>, View.OnClickListener {

    private TextView mInfoView;
    private Button mRestartButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_androscope);

        mInfoView = findViewById(R.id.text_androscope_info);
        mRestartButton = findViewById(R.id.button_androscope_restart);
        mRestartButton.setOnClickListener(this);

        startAndroscope();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Intent intent = new Intent(getApplication(), AndroscopeService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final AndroscopeService.LocalBinder localBinder = (AndroscopeService.LocalBinder) service;
        final AndroscopeService androscopeService = localBinder.getService();
        androscopeService.getStatusLiveData().observe(this, this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void onChanged(AndroscopeServiceStatus status) {
        mInfoView.setText(status.getMessage());
        mRestartButton.setVisibility(status.isRestartNeeded() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_androscope_restart) {
            startAndroscope();
        }
    }

    private void startAndroscope() {
        AndroscopeService.startServer(this);
    }
}
