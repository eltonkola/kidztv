package com.mrkola.kidztv.data


import android.graphics.Bitmap

data class Video(
    val id: String,  // Now using YouTube video ID as the primary ID
    val title: String,
    val filePath: String,
    val thumbnailPath: String? = null,
    val duration: Long = 0,
    val dateAdded: Long = System.currentTimeMillis()
)