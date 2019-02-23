package com.eltonkola.kidztv.data

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import com.eltonkola.kidztv.data.db.AppDatabase
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.model.settings.SettingsMenuItem
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.VideoElement
import com.eltonkola.kidztv.model.db.UserApp
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Single
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.lang.Exception


class VideoManager(private val context: Context) {

    val sdCardPath: String get() = AppFolder().sdCardPath

    fun loadVideos(): Single<List<VideoElement>> {

        return Single.create<List<VideoElement>> { emitter ->
            try {
                emitter.onSuccess(getVideos())
            } catch (e: Exception) {
                emitter.onError(e)
                e.printStackTrace()
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getVideos(): List<VideoElement> {
        val videos = File(sdCardPath)
            .walkTopDown()
            .filter { !it.isDirectory }
            .toList()
            .map { VideoElement(it, getThumbnail(it)) }
        return videos
    }

    fun deleteFile(element: VideoElement): Completable {
        return Completable.create { emitter ->
            try {
                val file = File(element.file.path)
                file.delete()
                if (file.exists()) {
                    file.canonicalFile.delete()
                    if (file.exists()) {
                        context.deleteFile(file.name)
                    }
                }
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }


    //this shit is so slow?? why??
    private fun getThumbnail(file: File): Bitmap {

//        return defaultArt
        val mmr = FFmpegMediaMetadataRetriever()

        return  try{
            mmr.setDataSource(file.absolutePath)
            mmr.getFrameAtTime(1, FFmpegMediaMetadataRetriever.OPTION_CLOSEST)
        }catch (e: Exception){
             defaultArt
         } finally {
             mmr.release()
        }


//        return try {
//            ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
//       } catch (e: Exception) {
//            defaultArt
//        }
    }

    private val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_thumnail)

}