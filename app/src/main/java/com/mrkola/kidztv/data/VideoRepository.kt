package com.mrkola.kidztv.data


import android.content.Context
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.downloader.Downloader
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VideoRepository(
    private val context: Context,
    val downloader: Downloader,
    private val videoDao: VideoDao
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val videosDir = File(context.filesDir, "videos")
    private val thumbnailsDir = File(context.filesDir, "thumbnails")

    init {
        videosDir.mkdirs()
        thumbnailsDir.mkdirs()
        
        // Background sync existing files to database if they're not there
        // This is a one-time migration
        repositoryScope.launch {
            syncWithFileSystem()
        }
    }

    private suspend fun syncWithFileSystem() {
        val files = videosDir.listFiles()?.filter { it.extension == "mp4" } ?: return
        files.forEach { file ->
            val youtubeId = file.nameWithoutExtension
            if (videoDao.getVideoById(youtubeId) == null) {
                val metaFile = File(videosDir, "$youtubeId.meta")
                val title = if (metaFile.exists()) metaFile.readText() else youtubeId
                val thumbnailFile = File(thumbnailsDir, "$youtubeId.jpg")
                
                val video = VideoEntity(
                    id = youtubeId,
                    title = title,
                    filePath = file.absolutePath,
                    thumbnailPath = if (thumbnailFile.exists()) thumbnailFile.absolutePath else null,
                    duration = getVideoDuration(file),
                    dateAdded = file.lastModified()
                )
                videoDao.insertVideo(video)
            }
        }
    }

    suspend fun getAllVideos(): List<Video> = withContext(Dispatchers.IO) {
        videoDao.getAllVideos().map { it.toVideo() }
    }

    fun getVideosFlow(): kotlinx.coroutines.flow.Flow<List<Video>> {
        return videoDao.getAllVideosFlow().map { entities ->
            entities.map { it.toVideo() }
        }
    }

    suspend fun deleteVideo(video: Video) = withContext(Dispatchers.IO) {
        val file = File(video.filePath)
        if (file.exists()) file.delete()
        video.thumbnailPath?.let {
            val thumbFile = File(it)
            if (thumbFile.exists()) thumbFile.delete()
        }
        videoDao.deleteVideo(video.toEntity())
    }

    suspend fun downloadVideo(url: String, onProgress: suspend (Int, Long) -> Unit = { _, _ -> }): Result<Video> =
        withContext(Dispatchers.IO) {
            try {
                val extractor = ServiceList.YouTube.getStreamExtractor(url)
                extractor.fetchPage()

                // Filter for video streams with audio, prefer 720p or lower to save space
                val videoStreams = extractor.videoStreams
                val bestStream = videoStreams
                    .filter { it.resolution.contains("720") || it.resolution.contains("480") || it.resolution.contains("360") }
                    .maxByOrNull { it.height }
                    ?: videoStreams.maxByOrNull { it.height } // Fallback to best if no 720p/lower
                    ?: return@withContext Result.failure(Exception("No video streams found"))

                val youtubeId = extractor.id
                val videoFile = File(videosDir, "$youtubeId.mp4")
                val thumbnailFile = File(thumbnailsDir, "$youtubeId.jpg")

                // Download video
                downloadFileWithProgress(bestStream.content, videoFile) { progress, speed ->
                    onProgress(progress, speed)
                }
                // Download thumbnail
                extractor.thumbnails.firstOrNull()?.let { thumbnail ->
                    downloadFile(thumbnail.url, thumbnailFile)
                }

                val video = Video(
                    id = youtubeId,
                    title = extractor.name,
                    filePath = videoFile.absolutePath,
                    thumbnailPath = thumbnailFile.absolutePath,
                    duration = extractor.length * 1000L,
                    dateAdded = System.currentTimeMillis()
                )

                videoDao.insertVideo(video.toEntity())

                Result.success(video)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun downloadFile(url: String, destination: File) {
        URL(url).openStream().use { input ->
            FileOutputStream(destination).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    private suspend fun downloadFileWithProgress(
        url: String,
        destination: File,
        onProgress: suspend (Int, Long) -> Unit
    ) = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection()
        connection.connect()

        val fileLength = connection.contentLengthLong
        val input = connection.getInputStream()
        val output = FileOutputStream(destination)

        try {
            val data = ByteArray(8192)
            var total: Long = 0
            var count: Int
            var lastUpdateTime = System.currentTimeMillis()
            var bytesSinceLastUpdate: Long = 0

            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                bytesSinceLastUpdate += count.toLong()
                
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTime >= 500) { // Update every 500ms
                    val timeDiff = (currentTime - lastUpdateTime) / 1000.0
                    val speed = (bytesSinceLastUpdate / timeDiff).toLong() // bytes per second
                    
                    if (fileLength > 0) {
                        val progress = ((total * 100) / fileLength).toInt()
                        onProgress(progress, speed)
                    } else {
                        onProgress(-1, speed) // Unknown total length
                    }
                    
                    lastUpdateTime = currentTime
                    bytesSinceLastUpdate = 0
                }
                output.write(data, 0, count)
            }
        } finally {
            input.close()
            output.close()
        }
    }

    private fun getVideoDuration(file: File): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            duration?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}

fun extractYouTubeVideoId(url: String): String {
    val regex = Regex(
        "(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})",
        RegexOption.IGNORE_CASE
    )

    return regex.find(url)?.groupValues?.get(1) ?: "007"
}
