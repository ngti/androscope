package nl.ngti.androscope.test

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button_grant_storage_permission).setOnClickListener(this)
        findViewById<View>(R.id.button_grant_contacts_permission).setOnClickListener(this)

        addSamplePreferences()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_grant_contacts_permission -> requestPermissions(Manifest.permission.READ_CONTACTS)
            R.id.button_grant_storage_permission -> requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun addSamplePreferences() {
        val prefs = getSharedPreferences("sample", Context.MODE_PRIVATE)
        if (!prefs.contains("sample_key")) {
            prefs.edit().putString("sample_key", "Sample Value").apply()
        }
    }

    private fun requestPermissions(vararg permissions: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 999)
        }
    }
}