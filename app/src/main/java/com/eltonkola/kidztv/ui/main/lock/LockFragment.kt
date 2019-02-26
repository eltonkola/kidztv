package com.eltonkola.kidztv.ui.main.lock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main_lock.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LockFragment : Fragment() {

    private val vm: LockViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main_lock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = "Unlock"

        otp_view.setOtpCompletionListener { otp ->

            if (otp != null && vm.isPinCorrect(otp)) {
                root_lock.visibility = View.GONE
                otp_view.setText("")
                startActivity(Intent(activity, SettingsActivity::class.java))
            } else {
                Toast.makeText(activity, "Error $otp, is the wrong code", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
