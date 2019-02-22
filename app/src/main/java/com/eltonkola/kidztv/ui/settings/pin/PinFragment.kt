package com.eltonkola.kidztv.ui.settings.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.fragment_settings_pin.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PinFragment : Fragment() {

    private val vm: PinViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_pin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setting_title.text = "Change pin: "

        vm.pin.observe(this, Observer {

            it?.let {

                if (it.toIntOrNull() != null && it.toInt() > -1) {
                    otp_view.hint =  it
                    setting_title.text = "Change pin:  ${it}"
                }else{
                    otp_view.hint = ""
                }
            }
        })

        but_reset.setOnClickListener {
            vm.setPin("-1")
        }

        otp_view.setOtpCompletionListener { otp ->
            vm.setPin(otp)
            Toast.makeText(activity, "New pin updated to ${otp} !!", Toast.LENGTH_SHORT).show()
        }

    }
}
