package com.eltonkola.kidztv.model

import android.content.Intent
import android.graphics.drawable.Drawable
import java.util.*

data class AppElement(val title: String, val icon: Drawable, val intent: Intent, val enabledDate: Date) {

//    val thumbnail: Bitmap by lazy {
//        ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
//    }
}
