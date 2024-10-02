package com.nakama.core.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nakama.core.R
import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Task
import com.nakama.core.databinding.ItemGalleryBinding
import com.nakama.core.databinding.ItemTaskBinding
import com.nakama.core.utils.Constants.DONE
import com.nakama.core.utils.Constants.IN_PROGRESS
import com.nakama.core.utils.getStatus
import com.nakama.core.utils.glide
import com.nakama.core.utils.gone
import com.nakama.core.utils.hide
import com.nakama.core.utils.show

@RequiresApi(Build.VERSION_CODES.M)
class GalleryAdapter: RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
  inner class GalleryViewHolder(private val binding: ItemGalleryBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(gallery: Gallery) {
      binding.apply {
        tvTitle.text = gallery.title
        ivThumbnail.glide(gallery.file.toString())
        btnPlay.setOnClickListener {
          onPlayClicked?.invoke(gallery)
        }
        btnEdit.setOnClickListener {
          onEditClicked?.invoke(gallery)
        }
      }
    }
  }

  private val diffUtil = object : DiffUtil.ItemCallback<Gallery>() {
    override fun areItemsTheSame(oldItem: Gallery, newItem: Gallery): Boolean =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Gallery, newItem: Gallery): Boolean =
      oldItem == newItem
  }

  val differ = AsyncListDiffer(this, diffUtil)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
    GalleryViewHolder(
      ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

  override fun getItemCount(): Int = differ.currentList.size

  override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
    val task = differ.currentList[position]
    holder.bind(task)
  }

  var onPlayClicked: ((Gallery) -> Unit)? = null
  var onEditClicked: ((Gallery) -> Unit)? = null
}