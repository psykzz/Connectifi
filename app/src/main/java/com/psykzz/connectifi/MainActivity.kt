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

    private var CHECK_COOLDOWN = false

    fun handleConnectionChanged(activity: AppCompatActivity) {
        if(CHECK_COOLDOWN == true) {
            Log.d("Connectifi", "On cooldown")
            return
        }
        CHECK_COOLDOWN = true
        Timer("Resetting the cooldown", true)
            .schedule(3000) {
                CHECK_COOLDOWN = false
                Log.d("Connectifi", "Timer reset")
            }

        Log.d("Connectifi", "Something happened?!")
        if(connectionTester.isConnected(activity)) {
            Log.d("Connectifi", "Found a valid connection to the internet")
            return
        }

        var webView: WebView = findViewById(R.id.webview)
        val wsu = WebSiteUtils(webView, "https://psykzz.com")
        // TODO: Try to press all the buttons in the wsu

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        connectionTester = ConnectionTester()



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

        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastReceiver)
    }
}