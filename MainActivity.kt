package com.ikey.ultimate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 38, 28, 28)
            setBackgroundColor(0xFFF5F5F7.toInt())
        }
        root.addView(TextView(this).apply { text="iKEY Ultimate"; textSize=30f; setTextColor(0xFF171717.toInt()) })
        root.addView(TextView(this).apply {
            text="Your iPhone-inspired keyboard for Android. Enable it, select it, then open any app and start typing."
            textSize=16f; setPadding(0,14,0,20)
        })
        root.addView(Button(this).apply {
            text="1. Enable iKEY Ultimate"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        root.addView(Button(this).apply {
            text="2. Select iKEY Ultimate"
            setOnClickListener { (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() }
        })
        root.addView(Button(this).apply {
            text="Keyboard settings"
            setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
        })
        root.addView(TextView(this).apply {
            text="\nPrivacy first: typed text stays on your device. This build does not upload keystrokes. Online services are not active yet."
            textSize=13f; setTextColor(0xFF555555.toInt())
        })
        setContentView(root)
    }
}
