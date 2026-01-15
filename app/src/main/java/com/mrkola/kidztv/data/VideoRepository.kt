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

class VideoRepository(private val context: Context, val downloader: Downloader) {
    private val videosDir = File(context.filesDir, "videos")
    private val thumbnailsDir = File(context.filesDir, "thumbnails")

    init {
        videosDir.mkdirs()
        thumbnailsDir.mkdirs()
    }

    suspend fun getAllVideos(): List<Video> = withContext(Dispatchers.IO) {
        videosDir.listFiles()
            ?.filter { it.extension == "mp4" }
            ?.map { file ->
                val metaFile = File(videosDir, "${file.nameWithoutExtension}.meta")
                val thumbnailFile = File(thumbnailsDir, "${file.nameWithoutExtension}.jpg")
                val metaContent = if (metaFile.exists()) metaFile.readLines() else emptyList()
                val title = metaContent.getOrNull(0) ?: file.nameWithoutExtension

                Video(
                    id = file.nameWithoutExtension,
                    title = title,
                    filePath = file.absolutePath,
                    thumbnailPath = if (thumbnailFile.exists()) thumbnailFile.absolutePath else null,
                    duration = getVideoDurationCached(file),
                    dateAdded = file.lastModified(),
                )
            }
            ?.sortedByDescending { it.dateAdded }
            ?: emptyList()
    }
    private val durationCache = mutableMapOf<String, Long>()

    private fun getVideoDurationCached(file: File): Long {
        return durationCache.getOrPut(file.absolutePath) {
            getVideoDuration(file)
        }
    }

    fun deleteVideo(video: Video) {
        File(video.filePath).delete()
        video.thumbnailPath?.let { File(it).delete() }
    }

    suspend fun downloadVideo(url: String, onProgress: (Int) -> Unit = {}): Result<Video> = withContext(Dispatchers.IO) {
        try {
            val extractor = ServiceList.YouTube.getStreamExtractor(url)
            extractor.fetchPage()

            val videoStreams = extractor.videoStreams
            val bestStream = videoStreams.maxByOrNull { it.height }
                ?: return@withContext Result.failure(Exception("No video streams found"))

            val youtubeId = extractor.id
            val videoFile = File(videosDir, "$youtubeId.mp4")
            val thumbnailFile = File(thumbnailsDir, "$youtubeId.jpg")

            // Download video
            downloadFileWithProgress(bestStream.content, videoFile) { progress ->
                onProgress(progress)
            }
            // Download thumbnail
            extractor.thumbnails.firstOrNull()?.let { thumbnail ->
                downloadFile(thumbnail.url, thumbnailFile)
            }

            // Save metadata (title)
            val metaFile = File(videosDir, "$youtubeId.meta")
            metaFile.writeText(extractor.name)

            val video = Video(
                id = youtubeId,
                title = extractor.name,
                filePath = videoFile.absolutePath,
                thumbnailPath = thumbnailFile.absolutePath,
                duration = extractor.length * 1000L
            )

            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun downloadFile(url: String, destination: File, onProgress: (Int) -> Unit = {}) {
        URL(url).openStream().use { input ->
            FileOutputStream(destination).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytes = 0L

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead
                }
            }
        }
    }

    private suspend fun downloadFileWithProgress(
        url: String,
        destination: File,
        onProgress: (Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection()
        connection.connect()

        val fileLength = connection.contentLengthLong
        val input = connection.getInputStream()
        val output = FileOutputStream(destination)

        try {
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                val progress = ((total * 100) / fileLength).toInt()
                withContext(Dispatchers.Main) {
                    onProgress(progress)
                }
                output.write(data, 0, count)
            }
        } finally {
            input.close()
            output.close()
        }
    }

    private fun extractTitle(file: File): String {
        val metaFile = File(videosDir, "${file.nameWithoutExtension}.meta")
        return if (metaFile.exists()) {
            metaFile.readText()
        } else {
            file.nameWithoutExtension
        }
    }

    private fun getThumbnailPath(file: File): String? {
        val thumbnailFile = File(thumbnailsDir, "${file.nameWithoutExtension}.jpg")
        return if (thumbnailFile.exists()) thumbnailFile.absolutePath else null
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
