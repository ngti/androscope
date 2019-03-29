package nl.ngti.androscope.test;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_grant_storage_permission).setOnClickListener(this);

        addSamplePreferences();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_grant_storage_permission) {
            requestStoragePermission();
        }
    }

    private void addSamplePreferences() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("sample", Context.MODE_PRIVATE);
        if (!prefs.contains("sample_key")) {
            prefs.edit().putString("sample_key", "Sample Value").apply();
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
        }
    }
}
