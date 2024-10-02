package com.nakama.todoupload.ui.media

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.nakama.core.utils.toast
import com.nakama.todoupload.R
import com.nakama.todoupload.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMediaBinding
  private lateinit var player: ExoPlayer

  companion object {
    const val VID_URL = "vid_url"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMediaBinding.inflate(layoutInflater)
    setContentView(binding.root)

    player = ExoPlayer.Builder(this).build()

    intent.getStringExtra(VID_URL)?.let { url ->
      binding.playerView.player = player

      val mediaItem = MediaItem.fromUri(url)
      player.setMediaItem(mediaItem)
      player.prepare()
      player.play()
    }

    player.addListener(object: Player.Listener{
      override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        toast("Error playing media")
      }
    })
  }

  override fun onStart() {
    super.onStart()
    player.playWhenReady = true
  }

  override fun onStop() {
    super.onStop()
    player.playWhenReady = false
  }

  override fun onDestroy() {
    super.onDestroy()
    player.release()
  }
}