package com.eltonkola.kidztv.ui.settings.appmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.ui.settings.appmanager.add.AddAppsDialogFragment
import kotlinx.android.synthetic.main.fragment_settings_app_manager.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppManagerFragment : Fragment() {


    private val ADD_APPS_TAG = "add_apps"
    private val vm: AppManagerViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_app_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting_title.text = "App Manager"

        app_list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        app_list.setHasFixedSize(true)

        activity?.let {
            app_list.adapter = AppListAdapter(it, true) { app ->
                vm.removeApp(app)
            }
        }
        vm.loading.observe(this, Observer { isLoading ->
            if (isLoading) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }
        })

        vm.apps.observe(this, Observer { data ->
            (app_list.adapter as AppListAdapter).setData(data)

            if(data.isEmpty()){
                no_apps.visibility = View.VISIBLE
            }else{
                no_apps.visibility = View.GONE
            }

        })
        vm.addOperation.observe(this, Observer {
            when(it){
                AppManagerViewModel.EditState.ADD_ERROR -> {
                    Toast.makeText(activity, "Error adding app", Toast.LENGTH_SHORT).show()
                    vm.setOperationReflected()
                }
                AppManagerViewModel.EditState.ADD_OK-> {
                    Toast.makeText(activity, "App saved to the white list", Toast.LENGTH_SHORT).show()
                    vm.setOperationReflected()
                }
                AppManagerViewModel.EditState.REMOVE_OK -> {
                    Toast.makeText(activity, "App removed from the white list", Toast.LENGTH_SHORT).show()
                    vm.setOperationReflected()
                }
                AppManagerViewModel.EditState.REMOVE_ERROR-> {
                    Toast.makeText(activity, "Error removing app", Toast.LENGTH_SHORT).show()
                    vm.setOperationReflected()
                }
                AppManagerViewModel.EditState.IDLE -> {
                    //do nothing
                }
            }
        })


        but_add_app.setOnClickListener {
            fragmentManager?.let {
                val ft = it.beginTransaction()
                val prev = it.findFragmentByTag(ADD_APPS_TAG)
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val newFragment = AddAppsDialogFragment.newInstance()
                newFragment.setTargetFragment(this, 1001)
                newFragment.show(ft, ADD_APPS_TAG)
            }
        }

    }

    fun addApp(app: AppElement) {
        vm.addApp(app)
    }

}
