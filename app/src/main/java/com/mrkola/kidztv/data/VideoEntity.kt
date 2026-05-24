package com.mrkola.kidztv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val filePath: String,
    val thumbnailPath: String?,
    val duration: Long,
    val dateAdded: Long
)

fun VideoEntity.toVideo() = Video(
    id = id,
    title = title,
    filePath = filePath,
    thumbnailPath = thumbnailPath,
    duration = duration,
    dateAdded = dateAdded
)

fun Video.toEntity() = VideoEntity(
    id = id,
    title = title,
    filePath = filePath,
    thumbnailPath = thumbnailPath,
    duration = duration,
    dateAdded = dateAdded
)
