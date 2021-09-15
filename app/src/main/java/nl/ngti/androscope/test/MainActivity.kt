package nl.ngti.androscope.test

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nl.ngti.androscope.AndroscopeActivity
import nl.ngti.androscope.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            bindViews()
        }

        addSamplePreferences()
    }

    private fun ActivityMainBinding.bindViews() {
        openAndroscopeActivity.setOnClickListener {
            openAndroscopeActivity()
        }
        buttonGrantStoragePermission.setOnClickListener {
            requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        buttonGrantContactsPermission.setOnClickListener {
            requestPermissions(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun addSamplePreferences() {
        getSharedPreferences("sample", Context.MODE_PRIVATE).run {
            if (!contains("sample_key")) {
                edit().putString("sample_key", "Sample Value").apply()
            }
        }
    }

    private fun requestPermissions(vararg permissions: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 999)
        }
    }

    private fun openAndroscopeActivity() {
        Intent(this, AndroscopeActivity::class.java).also {
            startActivity(it)
        }
    }
}
