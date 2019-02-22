package com.eltonkola.kidztv.ui.settings

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.BuildConfig
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.ui.settings.appmanager.AppManagerFragment
import com.eltonkola.kidztv.ui.settings.pin.PinFragment
import com.eltonkola.kidztv.ui.settings.stats.StatsFragment
import com.eltonkola.kidztv.ui.settings.videomanager.VideoManagerFragment
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.inject


class SettingsActivity : AppCompatActivity() {

    class SettingsItem(title: String, val external: Boolean, val icon: Drawable?, val fragment: Fragment? = null, val intent: Intent? = null) :
        MenuItem(title)

    open class MenuItem(val title: String) {
        override fun toString(): String = title
    }

    private val settingsItems = mutableListOf<MenuItem>()

    val appManager: AppManager by inject()

    fun initMenu() {

        settingsItems.add(MenuItem("App Settings"))
        settingsItems.add(
            SettingsItem(
                "Video Manager", false,
                ContextCompat.getDrawable(this, R.drawable.ic_subscriptions_black_24dp),
                VideoManagerFragment()
            )
        )
        settingsItems.add(
            SettingsItem(
                "App Manager",false,
                ContextCompat.getDrawable(this, R.drawable.ic_view_comfy_black_24dp),
                AppManagerFragment()
            )
        )
        settingsItems.add(
            SettingsItem(
                "Stats",false,
                ContextCompat.getDrawable(this, R.drawable.ic_graphic_eq_black_24dp),
                StatsFragment()
            )
        )
        settingsItems.add(
            SettingsItem(
                "Pin code",false,
                ContextCompat.getDrawable(this, R.drawable.ic_security_black_24dp),
                PinFragment()
            )
        )
        settingsItems.add(MenuItem("Plugins/Download videos"))


//        appManager.getPlugins().forEach {
//            settingsItems.add(appManager.toSettingItem(it))
//        }

        settingsItems.addAll(appManager.getPlugins().map { appManager.toSettingItem(it) })

        settingsItems.add(
            SettingsItem(
                "Get plugins",false,
                ContextCompat.getDrawable(this, R.drawable.ic_get_app_black_24dp),
                WebFragment.getFragment("Plugins", "file:///android_asset/plugins.html")
            )
        )

        settingsItems.add(MenuItem("Other"))
        settingsItems.add(
            SettingsItem(
                "About (v. ${BuildConfig.VERSION_NAME})",false,
                ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp),
                WebFragment.getFragment("About", "file:///android_asset/about.html")
            )
        )


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initMenu()
        item_list.adapter = MenuAdapter(this, settingsItems,
            View.OnClickListener { v ->
                openFragment(v.tag as SettingsItem)
            })
        openFragment(settingsItems[1] as SettingsItem)
    }

    private fun openFragment(item: SettingsItem) {
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
