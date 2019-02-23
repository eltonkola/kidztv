package com.eltonkola.kidztv.ui.settings.appmanager.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.settings.appmanager.AppListAdapter
import com.eltonkola.kidztv.ui.settings.appmanager.AppManagerFragment
import kotlinx.android.synthetic.main.fragment_settings_app_manager_add.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddAppsDialogFragment : DialogFragment() {

    private val vm: AddAppsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_app_manager_add, container, false)
    }

    companion object {

        internal fun newInstance(): AddAppsDialogFragment {
            val f = AddAppsDialogFragment()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting_title.text = "App App/Game"

        app_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        app_list.setHasFixedSize(true)


        activity?.let {
            app_list.adapter = AppListAdapter(it, false) { app ->
                (targetFragment as AppManagerFragment).addApp(app)
            }
        }

        vm.apps.observe(this, Observer { data ->
            (app_list.adapter as AppListAdapter).setData(data)
        })

        vm.loading.observe(this, Observer { isLoading ->
            if (isLoading) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }
        })


        but_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}