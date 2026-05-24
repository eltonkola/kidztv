package com.mrkola.kidztv.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val videoRepository: VideoRepository by inject()

    override suspend fun doWork(): Result {
        val videoUrl = inputData.getString(KEY_VIDEO_URL) ?: return Result.failure()

        return try {
            val result = videoRepository.downloadVideo(videoUrl) { progress, speed ->
                setProgress(workDataOf(
                    KEY_PROGRESS to progress,
                    KEY_SPEED to speed
                ))
            }

            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_VIDEO_URL = "video_url"
        const val KEY_PROGRESS = "progress"
        const val KEY_SPEED = "speed"
    }
}
