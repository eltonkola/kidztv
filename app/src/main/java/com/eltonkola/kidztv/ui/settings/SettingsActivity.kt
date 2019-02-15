package com.eltonkola.kidztv.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.BuildConfig
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    class SettingsItem(title: String, val icon: Int, val fragment: Fragment) : MenuItem(title)

    open class MenuItem(val title: String) {
        override fun toString(): String = title
    }


    val settingsItems = mutableListOf<MenuItem>()

    init {


        settingsItems.add(MenuItem("Settings"))
        settingsItems.add(SettingsItem("Video Manager", R.drawable.ic_subscriptions_black_24dp, VideoManagerFragment()))
        settingsItems.add(SettingsItem("App Manager", R.drawable.ic_view_comfy_black_24dp, PinFragment()))
        settingsItems.add(SettingsItem("Stats", R.drawable.ic_graphic_eq_black_24dp, PinFragment()))
        settingsItems.add(SettingsItem("Pin code", R.drawable.ic_security_black_24dp, PinFragment()))
        settingsItems.add(MenuItem("Plugins"))
        settingsItems.add(SettingsItem("Youtube Downloader", R.drawable.ic_cloud_download_black_24dp, PinFragment()))
        settingsItems.add(SettingsItem("Get plugins", R.drawable.ic_get_app_black_24dp, DownloadPluginsFragment()))
        settingsItems.add(MenuItem("Other"))
        settingsItems.add(
            SettingsItem(
                "About (v. ${BuildConfig.VERSION_NAME})",
                R.drawable.ic_info_outline_black_24dp,
                AboutFragment()
            )
        )


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        item_list.adapter = SimpleItemRecyclerViewAdapter(this, settingsItems,
            View.OnClickListener { v ->
                openFragment(v.tag as SettingsItem)
            })
        openFragment(settingsItems[1] as SettingsItem)
    }

    private fun openFragment(item: SettingsItem) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.item_detail_container, item.fragment)
            .commit()
    }


}
