package com.nakama.todoupload.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.UserModel
import com.nakama.core.utils.hide
import com.nakama.core.utils.show
import com.nakama.core.utils.toast
import com.nakama.core.utils.uriToFile
import com.nakama.todoupload.R
import com.nakama.todoupload.databinding.FragmentProfileBinding
import com.nakama.todoupload.ui.auth.LoginActivity
import com.nakama.todoupload.utils.showBottomNavView
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ProfileFragment : Fragment() {

  private var _binding: FragmentProfileBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<ProfileViewModel>()
  private lateinit var file: File
  private lateinit var user: UserModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    showBottomNavView()
    // Inflate the layout for this fragment
    _binding = FragmentProfileBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadUI()
    setupAction()
  }

  private fun loadUI() {
    viewModel.getUserData().observe(viewLifecycleOwner) {
      when(it) {
        is Resource.Loading -> {
          binding.progressBar.show()
          binding.ivProfil.hide()
        }
        is Resource.Success -> {
          user = it.data!!
          binding.apply {
            progressBar.hide()
            ivProfil.show()
            tvName.text = it.data?.name
            tvEmail.text = it.data?.email
            if (it.data?.profilePic.isNullOrEmpty()) {
              ivProfil.setImageResource(R.drawable.placeholder_user)
            } else {
              Glide.with(requireContext())
                .load(it.data?.profilePic)
                .into(ivProfil)
            }
          }
        }
        is Resource.Error -> {
          toast(it.message.toString())
        }
      }
    }
  }

  private fun setupAction() {
    binding.apply {
      btnLogout.setOnClickListener {
        viewModel.logOut()
        startActivity(
          Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          }
        )
      }
      tvChangeProfile.setOnClickListener {
        PickImageDialog.build(PickSetup())
          .setOnPickResult {
          if (it.error == null) {
            file = uriToFile(it.uri, context?.applicationContext!!)
            editProfilePic(user, file)
          } else {
            toast(it.error.message.toString())
            }
          }
          .setOnPickCancel {
            toast("Canceled")
          }.show(childFragmentManager)
        }
      }
    }

  private fun editProfilePic(user: UserModel, file: File) {
    viewModel.editProfilePic(user, file).observe(viewLifecycleOwner) {resource->
      when(resource) {
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.hide()
          toast(resource.data.toString())
          loadUI()
        }
        is Resource.Error -> {
          binding.progressBar.hide()
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}