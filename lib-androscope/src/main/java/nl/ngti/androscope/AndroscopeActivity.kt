package nl.ngti.androscope

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import nl.ngti.androscope.service.AndroscopeService
import nl.ngti.androscope.service.AndroscopeService.LocalBinder

/**
 * Starts Androscope (if it was not started yet) and shows its status.
 *
 * You can configure Androscope in manifest or resources,
 * see [https://github.com/ngti/androscope#recipes].
 */
class AndroscopeActivity : FragmentActivity(R.layout.activity_androscope), ServiceConnection, View.OnClickListener {

    private lateinit var infoView: TextView
    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        infoView = findViewById(R.id.text_androscope_info)
        restartButton = findViewById(R.id.button_androscope_restart)
        restartButton.setOnClickListener(this)

        startAndroscope()
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(application, AndroscopeService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val localBinder = service as LocalBinder
        localBinder.statusLiveData.observe(this, Observer { status ->
            infoView.text = status.message
            restartButton.visibility = if (status.isRestartNeeded) View.VISIBLE else View.GONE
        })
    }

    override fun onServiceDisconnected(name: ComponentName) {}

    override fun onClick(v: View) {
        if (v.id == R.id.button_androscope_restart) {
            startAndroscope()
        }
    }

    private fun startAndroscope() {
        AndroscopeService.startServer(this)
    }
}
