package com.eltonkola.kidztv.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eltonkola.kidztv.BuildConfig
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.model.settings.BaseMenuItem
import com.eltonkola.kidztv.model.settings.SettingsMenuItem
import com.eltonkola.kidztv.ui.settings.appmanager.AppManagerFragment
import com.eltonkola.kidztv.ui.settings.pin.PinFragment
import com.eltonkola.kidztv.ui.settings.stats.StatsFragment
import com.eltonkola.kidztv.ui.settings.videomanager.VideoManagerFragment
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.inject


class SettingsActivity : AppCompatActivity() {




    private val settingsItems = mutableListOf<BaseMenuItem>()

    val appManager: AppManager by inject()

    fun initMenu() {

        settingsItems.add(BaseMenuItem("App Settings"))
        settingsItems.add(
            SettingsMenuItem(
                "Video Manager", false,
                ContextCompat.getDrawable(this, R.drawable.ic_subscriptions_black_24dp),
                VideoManagerFragment()
            )
        )
        settingsItems.add(
            SettingsMenuItem(
                "App Manager", false,
                ContextCompat.getDrawable(this, R.drawable.ic_view_comfy_black_24dp),
                AppManagerFragment()
            )
        )
        settingsItems.add(
            SettingsMenuItem(
                "Stats", false,
                ContextCompat.getDrawable(this, R.drawable.ic_graphic_eq_black_24dp),
                StatsFragment()
            )
        )
        settingsItems.add(
            SettingsMenuItem(
                "Pin code", false,
                ContextCompat.getDrawable(this, R.drawable.ic_security_black_24dp),
                PinFragment()
            )
        )
        settingsItems.add(BaseMenuItem("Plugins/Download videos"))


//        appManager.getPlugins().forEach {
//            settingsItems.add(appManager.toSettingItem(it))
//        }

        settingsItems.addAll(appManager.getPlugins().map { appManager.toSettingItem(it) })

        settingsItems.add(
            SettingsMenuItem(
                "Get plugins", false,
                ContextCompat.getDrawable(this, R.drawable.ic_get_app_black_24dp),
                WebFragment.getFragment("Plugins", "file:///android_asset/plugins.html")
            )
        )

        settingsItems.add(BaseMenuItem("Other"))
        settingsItems.add(
            SettingsMenuItem(
                "System settings", true,
                ContextCompat.getDrawable(this, R.drawable.ic_settings_24dp),
                null, Intent(android.provider.Settings.ACTION_SETTINGS)
            )
        )
        settingsItems.add(
            SettingsMenuItem(
                "About (v. ${BuildConfig.VERSION_NAME})", false,
                ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp),
                WebFragment.getFragment("About", "file:///android_asset/about.html")
            )
        )


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initMenu()
        item_list.adapter = MenuAdapter(settingsItems,
            View.OnClickListener { v ->
                openFragment(v.tag as SettingsMenuItem)
            })
        openFragment(settingsItems[1] as SettingsMenuItem)
    }

    private fun openFragment(item: SettingsMenuItem) {
        item.fragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.item_detail_container, it)
                .commit()
        }

        item.intent?.let {
            startActivity(it)
        }

    }


}
