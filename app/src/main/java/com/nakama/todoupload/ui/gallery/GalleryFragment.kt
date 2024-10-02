package com.nakama.todoupload.ui.gallery

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Resource
import com.nakama.core.ui.GalleryAdapter
import com.nakama.core.utils.hide
import com.nakama.core.utils.setupRecyclerView
import com.nakama.core.utils.show
import com.nakama.core.utils.toast
import com.nakama.todoupload.databinding.FragmentGalleryBinding
import com.nakama.todoupload.ui.media.EditMediaActivity
import com.nakama.todoupload.ui.media.MediaActivity
import com.nakama.todoupload.utils.showBottomNavView
import org.koin.androidx.viewmodel.ext.android.viewModel

@RequiresApi(Build.VERSION_CODES.M)
class GalleryFragment : Fragment() {

  private var _binding: FragmentGalleryBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<GalleryViewModel>()
  private lateinit var galleryAdapter: GalleryAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    showBottomNavView()
    // Inflate the layout for this fragment
    _binding = FragmentGalleryBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.btnAddGallery.setOnClickListener {
      findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToCreateGalleryFragment())
    }
    observeUserGallery()
  }


  private fun observeUserGallery() {
    viewModel.getUserGallery().observe(viewLifecycleOwner) {resource ->
      when(resource) {
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.hide()
          resource.data?.let {
            initRv()
            setUpDataRv(it)
          }
        }
        is Resource.Error -> {
          binding.progressBar.hide()
          toast(resource.message ?: "Terjadi kesalahan")
        }
      }
    }
  }

  private fun initRv() {
    galleryAdapter = GalleryAdapter()

    setupRecyclerView(binding.rvTask, galleryAdapter) {position ->
      val gallery = galleryAdapter.differ.currentList[position]
      deleteGallery(gallery)
    }
  }

  private fun setUpDataRv(gallery: List<Gallery>) {
    galleryAdapter.differ.submitList(gallery)
    binding.emptyView.visibility = if (gallery.isEmpty()) View.VISIBLE else View.GONE
    setRvActions()
  }

  private fun setRvActions() {
    galleryAdapter.onPlayClicked = {
      playVideo(it.file!!)
    }
    galleryAdapter.onEditClicked = {
      startActivity(Intent(requireContext(), EditMediaActivity::class.java).apply {
        putExtra(MediaActivity.VID_URL, it.file)
      })
    }
  }

  private fun playVideo(file: String) {
    startActivity(Intent(requireContext(), MediaActivity::class.java).apply {
      putExtra(MediaActivity.VID_URL, file)
    })
  }

  private fun deleteGallery(gallery: Gallery) {
    viewModel.deleteGallery(gallery).observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Error -> {
          binding.progressBar.hide()
          toast(it.message ?: "Terjadi kesalahan")
        }
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.hide()
          toast(it.data ?: "")
          observeUserGallery()
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}