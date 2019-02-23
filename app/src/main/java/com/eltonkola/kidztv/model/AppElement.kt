package com.eltonkola.kidztv.model

import android.content.Intent
import android.graphics.drawable.Drawable
import com.eltonkola.kidztv.model.db.UserApp
import java.util.*

data class AppElement(val packageName: String, val title: String, val icon: Drawable, val intent: Intent, val enabledDate: Date) {
    fun toDbModel(): UserApp {
        return UserApp(packageName = packageName, enabledDate = enabledDate)
    }

//    val thumbnail: Bitmap by lazy {
//        ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
//    }
}
