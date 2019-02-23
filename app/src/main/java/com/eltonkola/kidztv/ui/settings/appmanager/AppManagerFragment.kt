package com.eltonkola.kidztv.ui.settings.appmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.fragment_settings_app_manager.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppManagerFragment : Fragment() {


    private val vm: AppManagerViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_app_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting_title.text = "App Manager"

        but_add_app.setOnClickListener {

        }
    }

}
