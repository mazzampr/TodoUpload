package com.nakama.todoupload.ui.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.utils.glide
import com.nakama.core.utils.hide
import com.nakama.core.utils.show
import com.nakama.core.utils.showDatePicker
import com.nakama.core.utils.toast
import com.nakama.core.utils.uriToFile
import com.nakama.todoupload.R
import com.nakama.todoupload.databinding.FragmentCreateGalleryBinding
import com.nakama.todoupload.utils.hideBottomNavView
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class CreateGalleryFragment : Fragment() {
  private var _binding: FragmentCreateGalleryBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<GalleryViewModel>()
  private var file: File? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    hideBottomNavView()
    // Inflate the layout for this fragment
    _binding = FragmentCreateGalleryBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupAction()
  }

  private fun setupAction() {
    binding.apply {

      cvFile.setOnClickListener {
        PickImageDialog.build(PickSetup().setVideo(true))
          .setOnPickResult {
            if (it.error == null) {
              file = uriToFile(it.uri, context?.applicationContext!!)
              ivPreview.glide(it.uri.toString())
              ivIconAdd.hide()
              toast("File berhasil dipilih")
            } else {
              toast(it.error.message.toString())
            }
          }

          .setOnPickCancel {
            toast("Canceled")
          }.show(childFragmentManager)
      }

      btnBack.setOnClickListener {
        findNavController().navigateUp()
      }

      btnCreate.setOnClickListener {
        if (inputNotValid()) {
          toast("Harap isi semua bagian")
          return@setOnClickListener
        }

        val gallery = Gallery(
          title = etTitle.text.toString()
        )
        addGallery(gallery)
      }
    }
  }


  private fun addGallery(gallery: Gallery) {
    viewModel.addGallery(gallery, file!!).observe(viewLifecycleOwner) {
      when(it) {
        is Resource.Loading -> {
          binding.apply {
            progressBar.show()
            btnCreate.hide()
          }
        }
        is Resource.Success -> {
          toast(it.data.toString())
          findNavController().navigateUp()
        }
        is Resource.Error -> {
          binding.apply {
            progressBar.hide()
            btnCreate.show()
          }
          toast(it.message.toString())
        }
      }
    }
  }

  private fun inputNotValid(): Boolean {
    return binding.etTitle.text.toString().isEmpty() || file == null
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}