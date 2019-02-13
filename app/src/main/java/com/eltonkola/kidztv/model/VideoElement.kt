package com.eltonkola.kidztv.model

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import java.io.File

data class VideoElement(val file: File) {

    val thumbnail: Bitmap by lazy {
        ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
    }

}