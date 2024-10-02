package com.nakama.todoupload.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.nakama.core.data.model.Resource
import com.nakama.core.utils.hide
import com.nakama.core.utils.show
import com.nakama.core.utils.toast
import com.nakama.todoupload.R
import com.nakama.todoupload.databinding.ActivityRegisterBinding
import com.nakama.todoupload.ui.home.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {
  private var _binding: ActivityRegisterBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<AuthViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    _binding = ActivityRegisterBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupAction()
  }

  private fun setupAction() {
    binding.apply {
      btnBack.setOnClickListener {
        finish()
      }
      tvNavigateLogin.setOnClickListener {
        finish()
      }

      btnRegister.setOnClickListener {
        val (isValid, errorMessage) = viewModel.validateRegisterForm(binding.edRegisterName, binding.edRegisterEmail, binding.edRegisterPassword)
        if (isValid) {
          registerNow()
        } else {
          if (errorMessage.isNotEmpty()) {
            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

  private fun registerNow() {
    viewModel.register(
      binding.edRegisterName.text.toString(),
      binding.edRegisterEmail.text.toString(),
      binding.edRegisterPassword.text.toString()
    ).observe(this) {
      when (it) {
        is Resource.Loading -> {
          binding.apply {
            progressBar.show()
            btnRegister.isEnabled = false
          }
        }

        is Resource.Success -> {
          binding.progressBar.hide()
          binding.btnRegister.isEnabled = true
          toast(it.data.toString())
          startActivity(
            Intent(this, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
          )
        }

        is Resource.Error -> {
          binding.apply {
            progressBar.hide()
            btnRegister.isEnabled = true
          }
          toast(it.message.toString())
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}