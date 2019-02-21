package com.eltonkola.kidztv.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import com.eltonkola.kidztv.ui.settings.SettingsActivity
import java.util.*

class AppManager(applicationContext: Context) {

    private val APP_PLUGIN = "com.eltonkola.kidstv.plugin.PLUGIN_APPLICATION"

    private val pm = applicationContext.packageManager

    fun getPlugins(): List<ActivityInfo> {
        val plugins = mutableListOf<ActivityInfo>()
        //dynamic plugins

        val queryIntent = Intent(Intent.ACTION_MAIN)
        queryIntent.addCategory(APP_PLUGIN)
        val infos = pm.queryIntentActivities(queryIntent, 0)
        for (resolveInfo in infos) {
            if (resolveInfo.activityInfo != null) {
                plugins.add(resolveInfo.activityInfo)
            }
        }
        return plugins.toList()
    }

    fun toSettingItem(app: ActivityInfo): SettingsActivity.SettingsItem {
        return SettingsActivity.SettingsItem(
            app.loadLabel(pm).toString(),
            pm.getApplicationIcon(app.packageName),
            null,
            getIntentForPackge(app.packageName)
        )
    }

    private fun getIntentForPackge(packageName: String): Intent {
        var intent = Intent()
        intent.setPackage(packageName)

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


}