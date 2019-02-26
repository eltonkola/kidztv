package com.eltonkola.kidztv.ui.main.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.activity_main_timer.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimerFragment : Fragment() {

    private val vm: TimerViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = "Unlock timer"

        otp_view.setOtpCompletionListener { otp ->

            if (otp != null && vm.isPinCorrect(otp)) {
                root_timer_lock.visibility = View.GONE
                otp_view.setText("")
                root_timer.visibility = View.VISIBLE
            } else {
                Toast.makeText(activity, "Error $otp, is the wrong code", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
