package com.eltonkola.kidztv.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.eltonkola.kidztv.R
import java.io.File

data class VideoElement(val file: File, val thumbnail: Bitmap )