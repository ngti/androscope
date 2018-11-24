package nl.ngti.androscope.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSamplePreferences();
    }

    private void addSamplePreferences() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("sample", Context.MODE_PRIVATE);
        if (!prefs.contains("sample_key")) {
            prefs.edit().putString("sample_key", "Sample Value").apply();
        }
    }

}
