package com.romreviewer.videoeditorcompose.ui.videoeditor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.nakama.todoupload.ui.media.PlayerControls
import com.nakama.todoupload.ui.media.VideoEditorViewModel

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun VideoEditorScreen(
    viewModel: VideoEditorViewModel,
    uri: String,
) {
    val context = LocalContext.current
    var sliderPosition by remember { mutableStateOf(0f..100f) }
    var videoPermissionState by remember {
        mutableStateOf(checkStoragePermission(context))
    }
    val state by viewModel.state.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
//    val storageActivityResultLauncher: ActivityResultLauncher<Intent> =
//        rememberLauncherForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) {
//            //Android is 11 (R) or above
//            if (Environment.isExternalStorageManager()) {
//                Log.i("MainActivity", "Storage Permissions Granted")
//                videoPermissionState = true
//                //Manage External Storage Permissions Granted
//            } else {
//                Toast.makeText(
//                    context,
//                    "Storage Permissions Denied",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
    val storageActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("VideoEditorScreen", "Activity result received: $result")
        videoPermissionState = checkStoragePermission(context)
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val uriVideo = uri
                val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                    context,
                    defaultDataSourceFactory
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uriVideo))
                setMediaSource(source)
                addAnalyticsListener(viewModel.analyticsExoPlayerListener())
                playWhenReady = false
                prepare()
            }
    }
//    LaunchedEffect(state.isBackPressed) {
//        if (state.isBackPressed) {
//            navController.popBackStack()
//        }
//    }
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            }
        )
        PlayerControls(
            onPlayPauseClicked = {
                isPlaying = !isPlaying
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
            },
            isPlaying = {
                isPlaying
            },
            currentPosition = sliderPosition,
            onSliderValueChanged = {
                if (it.start != sliderPosition.start)
                    exoPlayer.seekTo((it.start * 1000).toLong())
                sliderPosition = it
            },
            onSaveClicked = {
                viewModel.trimVideo(
                    (sliderPosition.start * 1000).toLong(),
                    (sliderPosition.endInclusive * 1000).toLong()
                )
            }
        )
        if (!videoPermissionState) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            Log.d("VideoEditorScreen", "Confirm button clicked")
                            runCatching {
                                Log.d("VideoEditorScreen", "Running intent creation")
                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                Log.d("VideoEditorScreen", "Intent created: $intent")
                                storageActivityResultLauncher.launch(intent)
                                Log.d("VideoEditorScreen", "Intent launched")
                            }.onFailure { e ->
                                Log.e("VideoEditorScreen", "Error launching intent", e)
                            }
                        }
                    ) {
                        Text(text = "Grant Permission")
                    }
                },
                title = {
                    Text(text = "Permission Required")
                },
                text = {
                    Text(text = "Write permission is required to to save edited video.")
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
        if (state.isExporting) {
            LoadingDialog(title = "Exporting Video")
        }

    }
}

fun checkStoragePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun LoadingDialog(
    title: String,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = {  }) {
        Column(modifier = modifier) {
            Text(text = title)
        }
    }
}
