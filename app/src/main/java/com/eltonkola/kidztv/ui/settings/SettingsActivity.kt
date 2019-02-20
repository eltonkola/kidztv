package com.eltonkola.kidztv.ui.settings

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.BuildConfig
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.settings.about.AboutFragment
import com.eltonkola.kidztv.ui.settings.pin.PinFragment
import com.eltonkola.kidztv.ui.settings.plugins.DownloadPluginsFragment
import com.eltonkola.kidztv.ui.settings.videomanager.VideoManagerFragment
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*


class SettingsActivity : AppCompatActivity() {

    class SettingsItem(title: String, val icon: Drawable?, val fragment: Fragment? = null, val intent: Intent? = null) :
        MenuItem(title)

    open class MenuItem(val title: String) {
        override fun toString(): String = title
    }

    private val settingsItems = mutableListOf<MenuItem>()
    val APP_PLUGIN = "com.eltonkola.kidstv.plugin.PLUGIN_APPLICATION"


    fun initMenu() {

        settingsItems.add(MenuItem("App Settings"))
        settingsItems.add(
            SettingsItem(
                "Video Manager",
                ContextCompat.getDrawable(this, R.drawable.ic_subscriptions_black_24dp),
                VideoManagerFragment()
            )
        )
        settingsItems.add(
            SettingsItem(
                "App Manager",
                ContextCompat.getDrawable(this, R.drawable.ic_view_comfy_black_24dp),
                PinFragment()
            )
        )
        settingsItems.add(
            SettingsItem(
                "Stats",
                ContextCompat.getDrawable(this, R.drawable.ic_graphic_eq_black_24dp),
                PinFragment()
            )
        )
        settingsItems.add(
            SettingsItem(
                "Pin code",
                ContextCompat.getDrawable(this, R.drawable.ic_security_black_24dp),
                PinFragment()
            )
        )
        settingsItems.add(MenuItem("Plugins/Download videos"))


        //dynamic plugins
        val pm = applicationContext.packageManager
        val queryIntent = Intent(Intent.ACTION_MAIN)
        queryIntent.addCategory(APP_PLUGIN)
        val infos = pm.queryIntentActivities(queryIntent, 0)
        for (resolveInfo in infos) {
            if (resolveInfo.activityInfo != null) {
                settingsItems.add(
                    SettingsItem(
                        resolveInfo.activityInfo.loadLabel(pm).toString(),
                        pm.getApplicationIcon(resolveInfo.activityInfo.packageName),
                        null,
                        getIntentForPackge(resolveInfo.activityInfo.packageName)
                    )
                )
            }
        }


        settingsItems.add(
            SettingsItem(
                "Get plugins",
                ContextCompat.getDrawable(this, R.drawable.ic_get_app_black_24dp),
                DownloadPluginsFragment()
            )
        )

        settingsItems.add(MenuItem("Other"))
        settingsItems.add(
            SettingsItem(
                "About (v. ${BuildConfig.VERSION_NAME})",
                ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp),
                AboutFragment()
            )
        )


    }

    fun getIntentForPackge(packageName: String): Intent {
        var intent = Intent()
        intent.setPackage(packageName)

        val pm = packageManager
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        Collections.sort(resolveInfos, ResolveInfo.DisplayNameComparator(pm))

        if (resolveInfos.size > 0) {
            val launchable = resolveInfos[0]
            val activity = launchable.activityInfo
            val name = ComponentName(
                activity.applicationInfo.packageName,
                activity.name
            )
            intent = Intent(Intent.ACTION_MAIN)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            intent.component = name


        }

        return intent
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
