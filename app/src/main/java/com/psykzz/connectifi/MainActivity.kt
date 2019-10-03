package com.psykzz.connectifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import android.os.StrictMode
import android.webkit.WebView
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private lateinit var broadCastReceiver: BroadcastReceiver
    private lateinit var connectionTester: ConnectionTester

    private var STATE_CHANGED_RECENTLY = false

    fun handleConnectionChanged(activity: AppCompatActivity) {
        Log.d("Connectifi", "Handling a network event")
        // We have a crude system to set a cool
        if(STATE_CHANGED_RECENTLY == true) {
            Log.d("Connectifi", "Change recently happened. Skipping...")
            return
        }
        STATE_CHANGED_RECENTLY = true
        Timer("Resetting STATE_CHANGED_RECENTLY", true)
            .schedule(3000) {
                STATE_CHANGED_RECENTLY = false
                Log.d("Connectifi", "STATE_CHANGED_RECENTLY reset")
            }


        if(connectionTester.isConnected(activity)) {
            Log.d("Connectifi", "Found a valid connection to the internet. No further action required")
            return
        }

        var webView: WebView = findViewById(R.id.webview)
        val wsu = WebSiteUtils(webView, "https://psykzz.com")
        // TODO: Try to press all the buttons in the wsu
    }

    fun registerHandlers() {
        var self = this

        val action = WifiManager.NETWORK_STATE_CHANGED_ACTION
        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    action -> handleConnectionChanged(self)
                }
            }
        }
        registerReceiver(
            broadCastReceiver,
            IntentFilter(action)
        )

        // Set this up to be used later for testing our connections
        connectionTester = ConnectionTester()
    }

    fun setPolicies() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register for the network change events
        registerHandlers()

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // This allows us to be bad and run main-thread network calls
        // TODO: Remove this once we go to background network calls.
        setPolicies()

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }
}