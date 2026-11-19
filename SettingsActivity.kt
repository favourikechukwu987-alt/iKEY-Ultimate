package com.ikey.ultimate

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.graphics.Color

class SettingsActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("ikey", MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(22,28,22,22); setBackgroundColor(0xFFF5F5F7.toInt()) }
        root.addView(TextView(this).apply { text="Keyboard Settings"; textSize=26f; setTextColor(Color.BLACK) })
        root.addView(TextView(this).apply { text="Personalise your keyboard"; textSize=14f; setPadding(0,6,0,18) })
        addSwitch(root, "Number row", "number_row", true, "Keep numbers above letters")
        addSwitch(root, "Key vibration", "vibration", true, "Haptic feedback when typing")
        addSwitch(root, "Key sounds", "sounds", false, "Play a soft key sound")
        addSwitch(root, "Auto-capitalisation", "autocap", true, "Capitalise the start of sentences")
        addSwitch(root, "Auto-correction", "autocorrect", true, "Correct a small built-in set of common typos")
        addSwitch(root, "Show suggestion strip", "suggestions", true, "Show suggestions above the keys")
        root.addView(TextView(this).apply { text="Keyboard appearance"; textSize=18f; setTextColor(Color.BLACK); setPadding(0,18,0,8) })
        val theme = Spinner(this)
        theme.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("iOS Light", "iOS Dark"))
        theme.setSelection(if (prefs.getString("theme","light")=="dark") 1 else 0)
        theme.onItemSelectedListener = object: android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
            override fun onItemSelected(p0: android.widget.AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) { prefs.edit().putString("theme", if(pos==1) "dark" else "light").apply() }
        }
        root.addView(theme)
        root.addView(TextView(this).apply {
            text="\nLanguages included: English, French and Spanish layouts (switch with the 🌐 key). Emoji availability and appearance depend partly on Android's installed font and OS version. Swipe typing, cloud predictions and voice dictation are not included in this build."
            textSize=13f; setTextColor(0xFF555555.toInt())
        })
        setContentView(ScrollView(this).apply { addView(root) })
    }
    private fun addSwitch(root: LinearLayout, title: String, key: String, default: Boolean, summary: String) {
        val row=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(0,8,0,8) }
        val sw=Switch(this).apply { text=title; isChecked=prefs.getBoolean(key,default); setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean(key,checked).apply() } }
        row.addView(sw)
        row.addView(TextView(this).apply { text=summary; textSize=12f; setTextColor(0xFF666666.toInt()) })
        root.addView(row)
    }
}
