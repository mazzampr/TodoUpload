package com.nakama.todoupload.ui.media

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.nakama.todoupload.R
import com.romreviewer.videoeditorcompose.ui.videoeditor.VideoEditorScreen

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("MissingInflatedId")
class EditMediaActivity : AppCompatActivity() {
  private lateinit var uri: String

  private val viewModel: VideoEditorViewModel by viewModels {
    VideoEditorViewModel.Factory(application, getVideoUri())
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_media)

    uri = intent.getStringExtra(MediaActivity.VID_URL).toString()

    val composeView = findViewById<ComposeView>(R.id.compose_view)
    composeView.setContent {
      VideoEditorScreen(viewModel = viewModel, uri = uri)
    }
  }

  private fun getVideoUri(): String {
    // Replace this with your actual logic to get the video URI
    return "${uri}"
  }
}