package com.mrkola.kidztv.data


import android.graphics.Bitmap

data class Video(
    val id: Long,
    val title: String,
    val filePath: String,
    val thumbnailPath: String? = null,
    val duration: Long = 0,
    val dateAdded: Long = System.currentTimeMillis()
)