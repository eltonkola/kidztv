package com.eltonkola.kidztv.views

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.widget.TextView


class CountDownView : TextView {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelTimer()
    }

    var sa = 10

    var timer: CountDownTimer? = null

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        text = sa.toString()

        timer = object : CountDownTimer(sa.toLong() * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                sa--
                text = sa.toString()
                if (sa > 0) {
                    onTick.invoke()
                }
            }

            override fun onFinish() {
                text = "X"
                onDone.invoke()
            }
        }
        timer?.start()


    }

    fun cancelTimer() {
        timer?.cancel()
    }


    var onDone: () -> Unit = {}
    var onTick: () -> Unit = {}

}
