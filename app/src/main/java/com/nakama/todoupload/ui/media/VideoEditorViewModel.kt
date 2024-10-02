package com.nakama.todoupload.ui.media

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(UnstableApi::class)
class VideoEditorViewModel(
    application: Application,
    private val uri: String
) : AndroidViewModel(application) {
    val state = MutableStateFlow(VideoEditorState())
    var initTime = 0L
    var playTime = 0L
    var pauseTime = 0L
    var pressedPaused = 0
    var totalTime = 0L

    fun analyticsExoPlayerListener() = object : AnalyticsListener {
        override fun onIsPlayingChanged(
            eventTime: AnalyticsListener.EventTime,
            isPlaying: Boolean
        ) {
            if (isPlaying) {
                if (initTime != 0L) pauseTime += System.currentTimeMillis() - initTime
                initTime = System.currentTimeMillis()
            } else {
                if (initTime != 0L) playTime += System.currentTimeMillis() - initTime
                initTime = System.currentTimeMillis()
                pressedPaused++
            }
            totalTime = playTime + pauseTime
            Log.e("onIsPlaying", "PLAYTIME: $playTime")
            Log.e("onIsPlaying", "PRESSEDPAUSE: $pressedPaused")
            Log.e("onIsPlaying", "PAUSETIME: $pauseTime")
            Log.e("onIsPlaying", "TOTALTIME: $totalTime")
            /*if (exoPlayer.currentPosition >= (exoPlayer.duration - (sliderPosition.endInclusive * 1000))) {
                exoPlayer.seekTo((exoPlayer.duration - (sliderPosition.endInclusive * 1000)).toLong())
                exoPlayer.pause()
            }*/
            super.onIsPlayingChanged(eventTime, isPlaying)
        }
    }

    private val transformerListener: Transformer.Listener =
        object : Transformer.Listener {
            override fun onCompleted(composition: Composition, result: ExportResult) {
                Log.i("VideoEditorViewModel", "Completed $result")
                updateExportLoading(false)
                Toast.makeText(getApplication(), "Video trimmed! at Android/data/com.nakama.todoupload/files/Movies/TRIM_timestamp.mp4", Toast.LENGTH_SHORT).show()
                state.update { it.copy(isBackPressed = true) }
            }

            override fun onError(
                composition: Composition, result: ExportResult,
                exception: ExportException
            ) {
                Log.e("VideoEditorViewModel", "Error $exception")
                updateExportLoading(false)
            }
        }

    fun trimVideo(startPosition: Long, endPosition: Long) {
        val context = getApplication<Application>()
        val outputFile = createOutputFile(context)
        val effect = arrayListOf<Effect>()

        val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(startPosition)
            .setEndPositionMs(endPosition)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setClippingConfiguration(clippingConfiguration)
            .build()

        val editedMediaItem = EditedMediaItem.Builder(mediaItem).apply {
            setEffects(Effects(mutableListOf(), effect))
        }

        val transformer = Transformer.Builder(context)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .addListener(transformerListener)
            .build()

        kotlin.runCatching {
            updateExportLoading(true)
            Log.d("VideoEditorViewModel", "Starting trim: start=$startPosition, end=$endPosition")
            transformer.start(editedMediaItem.build(), outputFile.absolutePath)
        }.onFailure { error ->
            Log.e("VideoEditorViewModel", "Error during trimming", error)
            updateExportLoading(false)
        }
    }

    private fun createOutputFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File(storageDir, "TRIM_$timeStamp.mp4")
    }

    private fun updateExportLoading(isLoading: Boolean) {
        state.update { it.copy(isExporting = isLoading) }
    }

    class Factory(
        private val application: Application,
        private val uri: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VideoEditorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VideoEditorViewModel(application, uri) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}