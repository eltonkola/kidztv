package com.eltonkola.kidztv.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.eltonkola.kidztv.R

class WindowView : RelativeLayout {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    var contentContainer: RelativeLayout? = null

    private lateinit var timer: CountDownView
    private lateinit var close: ImageView

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val root = LayoutInflater.from(context).inflate(R.layout.window_view, this)
        contentContainer = findViewById(R.id.content_container)

        timer = root.findViewById(R.id.countdow_view)
        close = root.findViewById(R.id.close)
        timer.onDone = {
            onDone.invoke()
        }

        timer.onTick = {
            onTick.invoke()
        }

        close.visibility = View.GONE
        close.setOnClickListener { onClose.invoke() }

    }

    var onClose: () -> Unit = {}
    var onDone: () -> Unit = {}

    private var onTick: () -> Unit = {
        alphaxx -= 0.05f
        val animation1 = AlphaAnimation(lastAlpha, alphaxx)
        animation1.duration = 1000
        animation1.fillAfter = true
        startAnimation(animation1)
        lastAlpha = alphaxx
    }

    var lastAlpha = 1.0f
    private var alphaxx = 1.0f

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (contentContainer == null) {
            super.addView(child, index, params)
        } else {
            contentContainer?.addView(child, index, params)
        }


    }

    fun setTitle(title: String) {
        findViewById<TextView>(R.id.window_title).text = title
    }

    fun cancelTimer() {
        timer.cancelTimer()

        timer.visibility = View.GONE
        close.visibility = View.VISIBLE

    }

}
