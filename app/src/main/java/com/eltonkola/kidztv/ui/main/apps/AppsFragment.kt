package com.eltonkola.kidztv.ui.main.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_main_apps.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppsFragment : Fragment() {

    private val vm: AppsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.loading_apps.observe(this, Observer { loadingApps ->
            if (loadingApps) {
                loading_apps.visibility = View.VISIBLE
            } else {
                loading_apps.visibility = View.GONE
            }
        })

        app_grid.layoutManager = GridLayoutManager(activity, 6)
        app_grid.setHasFixedSize(true)

        app_grid.adapter = AppsGridAdapter(activity!!) { app ->
            startActivity(vm.openApp(app))
        }

        vm.apps.observe(this, Observer { apps ->
            if (apps.isEmpty()) {
                no_apps.visibility = View.VISIBLE
            } else {
                no_apps.visibility = View.GONE
                (app_grid.adapter as AppsGridAdapter).setData(apps)
            }
        })

        root_apps.setTitle("Apps")
        root_apps.onDone = {
            (activity as MainActivity).resetExtraUi()
        }

    }
}
