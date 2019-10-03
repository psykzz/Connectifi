package com.psykzz.connectifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL
import android.webkit.WebViewClient
import android.webkit.WebChromeClient


class ConnectionTester {

    /*
    * Validates the network is a wifi network
    * */
    private fun verifyAvailableNetwork(activity: AppCompatActivity) : Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /*
    * Tries to do a network request to the given url
    * */
    private fun performInternetTest(url: URL) : Boolean {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            Log.d("Connectifi", "\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            return true
        }
        return false
    }

    /*
    * First checks wifi and if connected tests a url for an valid & expected connection */
    fun isConnected(activity: AppCompatActivity):Boolean {
        if(!verifyAvailableNetwork(activity)) {
            Log.d("Connectifi", "Not on wifi, skipping...")
            return false
        }

        return performInternetTest(URL("https://www.psykzz.com"))
    }
}

/*
* This is a helper class that manages a webview and tries to run javascript to
* press buttons tick boxes and try to get us connected to the internet*/
class WebSiteUtils {
    private var webView: WebView

    constructor(webView: WebView, url: String) {
        this.webView = webView
        this.webView.getSettings().setJavaScriptEnabled(true)
        this.webView.setWebChromeClient(WebChromeClient())
        this.webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val javaScript = "javascript:(function() {alert('hello');})()"
                view.loadUrl(javaScript)
            }
        })
        this.webView.loadUrl(url)
    }

    private fun ensurePage() {

    }

    private fun clickAllButtons() {

    }
}