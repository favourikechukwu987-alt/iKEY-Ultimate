package com.ikey.ultimate

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.content.Context
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import java.util.Locale

class IKeyInputMethodService : InputMethodService() {
    private lateinit var root: LinearLayout
    private var symbols=false
    private var emojiMode=false
    private var clipboardMode=false
    private var shift=true
    private var language=0
    private val prefs by lazy { getSharedPreferences("ikey", Context.MODE_PRIVATE) }
    private val lightKeys=0xFFFFFFFF.toInt()
    private val darkKeys=0xFF3A3A3C.toInt()
    private val emojiGroups = listOf(
        "😀" to listOf("😀","😃","😄","😁","😆","😅","😂","🤣","😊","😇","🙂","🙃","😉","😍","🥰","😘","😋","😎","🤩","🥳","😭","😢","😡","🤔","🤗","🤫","🤭","🫡","😴","😱","🤯","🙄"),
        "❤️" to listOf("❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔","💕","💞","💓","💗","💖","💘","💝","💟","❣️","💯","🔥","✨","⭐","🌟","🎉","🎊"),
        "🐻" to listOf("🐶","🐱","🐭","🐹","🐰","🦊","🐻","🐼","🐨","🐯","🦁","🐮","🐷","🐸","🐵","🐔","🐧","🦄","🐝","🦋","🐢","🐬","🦖"),
        "🍕" to listOf("🍎","🍐","🍊","🍋","🍌","🍉","🍇","🍓","🍒","🍍","🥭","🍑","🥑","🍔","🍟","🍕","🌭","🍿","🍩","🎂","☕","🧋"),
        "⚽" to listOf("⚽","🏀","🏈","⚾","🎾","🏐","🏉","🎱","🏓","🏸","🥇","🏆","🎮","🎧","🎤","🎬","📱","💻","🚗","✈️"),
        "🚗" to listOf("🚗","🚕","🚌","🚎","🏎️","🚓","🚑","🚒","🚲","🛴","✈️","🚀","🚢","🏠","🏫","🏥","🏖️","🌍","🌙","☀️")
    )
    private val symbolsRows=listOf(
        listOf("1","2","3","4","5","6","7","8","9","0"),
        listOf("@","#","$","%","&","-","+","(",")","/"),
        listOf("=\\<","*","\"","'",":",";","!","?","⌫"),
        listOf("ABC",",","😊","🌐","space",".","↵")
    )
    private val layouts=listOf(
        listOf(listOf("q","w","e","r","t","y","u","i","o","p"),listOf("a","s","d","f","g","h","j","k","l"),listOf("⇧","z","x","c","v","b","n","m","⌫")),
        listOf(listOf("a","z","e","r","t","y","u","i","o","p"),listOf("q","s","d","f","g","h","j","k","l","m"),listOf("⇧","w","x","c","v","b","n","⌫")),
        listOf(listOf("q","w","e","r","t","y","u","i","o","p"),listOf("a","s","d","f","g","h","j","k","l"),listOf("⇧","z","x","c","v","b","n","m","⌫"))
    )
    override fun onCreateInputView(): View {
        root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(4),dp(5),dp(4),dp(6)) }
        render()
        return root
    }
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) { super.onStartInputView(info,restarting); render() }

    private fun render() {
        if (!::root.isInitialized) return
        val dark=prefs.getString("theme","light")=="dark"
        val bg=if(dark) 0xFF202124.toInt() else 0xFFD1D5DB.toInt()
        val keyColor=if(dark) darkKeys else lightKeys
        root.removeAllViews(); root.setBackgroundColor(bg)
        if(emojiMode) { renderEmoji(keyColor,dark); return }
        if(clipboardMode) { renderClipboard(keyColor,dark); return }
        if(prefs.getBoolean("suggestions",true)) {
            val bar=LinearLayout(this).apply { gravity=Gravity.CENTER; orientation=LinearLayout.HORIZONTAL }
            addKey(bar,"▣",1f,keyColor,dark){ clipboardMode=true; render() }
            addKey(bar,"iKEY",3f,keyColor,dark){ }
            addKey(bar,"😊",1f,keyColor,dark){ emojiMode=true; render() }
            addKey(bar,"⚙",1f,keyColor,dark){ startActivity(Intent(this@IKeyInputMethodService,SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            root.addView(bar,LinearLayout.LayoutParams(-1,dp(38)))
        }
        if(prefs.getBoolean("number_row",true)) {
            val line=makeRow()
            ("1234567890").forEach { ch -> addKey(line,ch.toString(),1f,keyColor,dark){ type(ch.toString()) } }
            root.addView(line)
        }
        val active=if(symbols) symbolsRows else layouts[language]
        active.forEach { row ->
            val line=makeRow()
            row.forEach { key ->
                val weight=if(key in listOf("⇧","⌫","ABC","=\\<")) 1.35f else 1f
                addKey(line,key,weight,keyColor,dark){ handle(key) }
            }
            root.addView(line)
        }
        val bottom=makeRow()
        listOf("," to 0.8f,"😊" to 1.05f,"🌐" to 1.0f,"space" to 4.2f,"." to 0.8f,"↵" to 1.35f).forEach { (k,w) ->
            addKey(bottom,k,w,keyColor,dark){ handle(k) }
        }
        root.addView(bottom)
    }
    private fun renderEmoji(keyColor:Int,dark:Boolean) {
        val bar=makeRow()
        addKey(bar,"ABC",1.2f,keyColor,dark){ emojiMode=false; render() }
        emojiGroups.forEach { (icon,_) -> addKey(bar,icon,1f,keyColor,dark){ showEmojiGroup(icon,keyColor,dark) } }
        root.addView(bar)
        val scroller=ScrollView(this)
        val grid=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        emojiGroups.first().second.chunked(8).forEach { group ->
            val row=makeRow()
            group.forEach { e -> addKey(row,e,1f,keyColor,dark){ currentInputConnection?.commitText(e,1); feedback() } }
            grid.addView(row)
        }
        scroller.addView(grid); root.addView(scroller,LinearLayout.LayoutParams(-1,dp(205)))
    }
    private fun showEmojiGroup(icon:String,keyColor:Int,dark:Boolean) {
        val scroller=root.getChildAt(1) as? ScrollView ?: return
        val grid=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        val items=emojiGroups.firstOrNull{it.first==icon}?.second ?: emojiGroups.first().second
        items.chunked(8).forEach { group ->
            val row=makeRow(); group.forEach { e -> addKey(row,e,1f,keyColor,dark){ currentInputConnection?.commitText(e,1); feedback() } }; grid.addView(row)
        }
        scroller.removeAllViews(); scroller.addView(grid)
    }
    private fun renderClipboard(keyColor:Int,dark:Boolean) {
        val top=makeRow(); addKey(top,"ABC",1f,keyColor,dark){ clipboardMode=false; render() }; addKey(top,"Paste copied text",4f,keyColor,dark){ pasteClipboard() }; root.addView(top)
        val manager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text=if(manager.hasPrimaryClip()) manager.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString().orEmpty() else ""
        root.addView(TextView(this).apply {
            this.text=if(text.isBlank()) "No clipboard text available" else text.take(160)
            textSize=15f; setTextColor(if(dark) Color.WHITE else Color.DKGRAY); setPadding(dp(12),dp(16),dp(12),dp(16))
            setOnClickListener { pasteClipboard() }
        })
    }
    private fun pasteClipboard() {
        val cm=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val value=if(cm.hasPrimaryClip()) cm.primaryClip?.getItemAt(0)?.coerceToText(this) else null
        if(value!=null) currentInputConnection?.commitText(value,1)
        clipboardMode=false; render()
    }
    private fun handle(k:String) {
        when(k) {
            "space" -> { val ic=currentInputConnection; val before=ic?.getTextBeforeCursor(40,0)?.toString().orEmpty(); val word=before.substringAfterLast(' '); val fix=if(prefs.getBoolean("autocorrect",true)) corrections[word.lowercase(Locale.ROOT)] else null
                if(fix!=null) { ic?.deleteSurroundingText(word.length,0); ic?.commitText(fix,1) }; ic?.commitText(" ",1); shift=prefs.getBoolean("autocap",true) }
            "↵" -> currentInputConnection?.performEditorAction(currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE)
            "⌫" -> currentInputConnection?.deleteSurroundingText(1,0)
            "⇧" -> shift=!shift
            "123" -> symbols=true
            "ABC" -> symbols=false
            "=\\<" -> symbols=true
            "😊" -> { emojiMode=true; render() }
            "🌐" -> { language=(language+1)%layouts.size; symbols=false }
            else -> type(k)
        }
        feedback(); if(!emojiMode && !clipboardMode) render()
    }
    private fun type(value:String) {
        val output=if(value.length==1 && value[0].isLetter() && shift) value.uppercase(Locale.ROOT) else value
        currentInputConnection?.commitText(output,1)
        if(value.length==1 && value[0].isLetter()) shift=false
    }
    private fun makeRow()=LinearLayout(this).apply { gravity=Gravity.CENTER; setPadding(0,dp(2),0,dp(2)) }
    private fun addKey(parent:LinearLayout,label:String,weight:Float,color:Int,dark:Boolean,action:()->Unit) {
        val display=when(label){"space"->" "; "⇧"->"⇧"; else->label}
        val key=TextView(this).apply {
            text=display; textSize=if(label.length>2 && label!="space") 13f else 18f
            gravity=Gravity.CENTER; setTextColor(if(dark) Color.WHITE else 0xFF202124.toInt())
            typeface=Typeface.DEFAULT
            background=GradientDrawable().apply { setColor(if(label in listOf("⇧","⌫","123","ABC","↵","=\\<","🌐")) { if(dark) 0xFF55565A.toInt() else 0xFFADB4BE.toInt() } else color); cornerRadius=dp(6).toFloat() }
            elevation=dp(1).toFloat()
            setOnClickListener { action() }
            setOnLongClickListener { if(label=="space") { language=(language+1)%layouts.size; render(); true } else if(label=="⌫") { currentInputConnection?.deleteSurroundingText(10,0); true } else false }
        }
        parent.addView(key,LinearLayout.LayoutParams(0,dp(43),weight).apply { setMargins(dp(2),dp(2),dp(2),dp(2)) })
    }
    private fun feedback() {
        if(prefs.getBoolean("vibration",true)) {
            val v=getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if(Build.VERSION.SDK_INT>=26) v.vibrate(VibrationEffect.createOneShot(12,35)) else @Suppress("DEPRECATION") v.vibrate(12)
        }
    }
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
    private val corrections=mapOf("teh" to "the","adn" to "and","thier" to "their","recieve" to "receive","dont" to "don't","cant" to "can't","wont" to "won't","im" to "I'm")
}
