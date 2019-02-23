package com.eltonkola.kidztv.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import com.eltonkola.kidztv.data.db.AppDatabase
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.model.settings.SettingsMenuItem
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.eltonkola.kidztv.model.db.UserApp
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.lang.Exception


class AppManager(applicationContext: Context, private val appDatabase: AppDatabase) {

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

    fun getWhitelistedApps(): Flowable<List<AppElement>> {
        return appDatabase.userAppDao().getAllRx()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.map { dbModelToAppElement(it) }  }
    }

//    fun getWhitelistedApps(): Flowable<List<AppElement>> {
//        return appDatabase.userAppDao().getAllRx()
//            .flatMapIterable { it }
//            .map { dbModelToAppElement(it) }
//            .toList()
//            .toFlowable()
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//    }

//    fun getWhitelistedApps(): Flowable<List<AppElement>> {
//     return Flowable.create<List<AppElement>>({ emitter ->
//
//         appDatabase.userAppDao().getAllRx()
//             .subscribeOn(Schedulers.newThread())
//             .observeOn(AndroidSchedulers.mainThread())
//             .subscribe({
//                 emitter.onNext(it.map { dbModelToAppElement(it) })
//             },{
//                 emitter.onError(it)
//             })
//
//
//
//     }, BackpressureStrategy.LATEST)
//         .subscribeOn(Schedulers.newThread())
//         .observeOn(AndroidSchedulers.mainThread())
//
//    }


    private fun getPackageInfoByName(packageName: String): ApplicationInfo? {
        var ai: ApplicationInfo?
        try {
            ai = pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            ai = null
        }
        return ai
    }

    private fun getAppName(ai: ApplicationInfo?): String {
        return if (ai != null) pm.getApplicationLabel(ai).toString() else "(unknown)"
    }

    fun addAppToWhitelist(app: AppElement): Completable {
        return appDatabase.userAppDao()
            .insert(app.dbModel)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /*
       all apps i can add, not already on db
    */
    //all apps take long to load icons, so will cache them
    //TODO add event listener for package install/remove to invalidate this list
    val allapps = mutableListOf<AppElement>()

    private fun getAllApps(){
        if(allapps.isEmpty()){
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = pm.queryIntentActivities(intent, 0)
            allapps.addAll(apps.map {toAppElement(it.activityInfo) })
        }
    }

    fun getAddableApps(): Flowable<List<AppElement>> {
        return Flowable.create<List<AppElement>> ({ task ->
            try {
                //load all installed app, if not in cache
                getAllApps()
                val plugins = getPlugins().map { toAppElement(it) }

                appDatabase.userAppDao()
                    .getAllRx()
                    .map { it.map { dbModelToAppElement(it)}}
                    .subscribe({ allAddedApps ->

                        val allNotPluginApps = allapps.filter { !plugins.contains(it) }
                        val allAppsWeCanAdd = allNotPluginApps.filter { app ->
                            allAddedApps.none { it.packageName == app.packageName }
                        }

                        task.onNext(allAppsWeCanAdd)

                    },{
                        task.onError(it)
                    })

            } catch (e: Exception) {
                task.onError(e)
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun dbModelToAppElement(app: UserApp): AppElement {
        val ai = getPackageInfoByName(app.packageName)
        return AppElement(
            app.packageName,
            getAppName(ai),
            pm.getApplicationIcon(app.packageName),
            app
        )
    }

    fun toAppElement(app: ActivityInfo): AppElement {
        return AppElement(app.packageName,
            app.loadLabel(pm).toString(),
            pm.getApplicationIcon(app.packageName),
            UserApp(app.packageName, Date())
        )
    }

    fun removeAppFromWhitelist(app: AppElement): Completable {
        return appDatabase.userAppDao()
            .delete(app.dbModel)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }
}