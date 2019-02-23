package com.eltonkola.kidztv.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import com.eltonkola.kidztv.data.db.AppDatabase
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.model.settings.SettingsMenuItem
import com.eltonkola.kidztv.model.db.UserApp
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import sun.invoke.util.VerifyAccess.getPackageName
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager



class AppManager(applicationContext: Context, private val appDatabase : AppDatabase) {

    //will use this to list all installed apps plugins
    private val APP_PLUGIN = "com.eltonkola.kidstv.plugin.PLUGIN_APPLICATION"

    private val pm = applicationContext.packageManager

    /*
    * list all app plugins installed on the phone
    * */
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

    /*
   * transform to the model we use to show on settings menu
   * */
    fun toSettingItem(app: ActivityInfo): SettingsMenuItem {
        return SettingsMenuItem(
            app.loadLabel(pm).toString(), true,
            pm.getApplicationIcon(app.packageName),
            null,
            getIntentForPackage(app.packageName)
        )
    }

    /*
   * get app launch intent
   * */
    private fun getIntentForPackage(packageName: String): Intent {
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

    /*
    list all whitelisted apps
    */

    fun getWhitelistedApps() : Flowable<List<AppElement>>  {
        return appDatabase.userAppDao().getAllRx()
            .flatMapIterable{it}
            .map {

                AppElement("", null, getIntentForPackage(it.packageName), Date() )
            }
        .toList()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getPacageInfoBYName(packageName: String) : ApplicationInfo?{
        var ai: ApplicationInfo?
        try {
            ai = pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            ai = null
        }
    }


    val applicationName = if (ai != null) pm.getApplicationLabel(ai) else "(unknown)"
}