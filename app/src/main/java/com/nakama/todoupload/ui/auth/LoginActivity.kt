package com.nakama.todoupload.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.nakama.core.data.model.Resource
import com.nakama.core.utils.hide
import com.nakama.core.utils.show
import com.nakama.core.utils.toast
import com.nakama.todoupload.R
import com.nakama.todoupload.databinding.ActivityLoginBinding
import com.nakama.todoupload.ui.home.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

  private var _binding: ActivityLoginBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<AuthViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    _binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    setupAction()

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }
  }

  private fun setupAction() {
    binding.apply {
      tvNavigateRegister.setOnClickListener {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
      }

      btnLogin.setOnClickListener {
        val (isValid, errorMessage) = viewModel.validateLoginForm(binding.edLoginEmail, binding.edLoginPassword)
        if (isValid) {
          loginNow()
        } else {
          if (errorMessage.isNotEmpty()) {
            toast(errorMessage)
          }
        }
      }
    }
  }

  private fun loginNow() {
    viewModel.login(
      binding.edLoginEmail.text.toString(),
      binding.edLoginPassword.text.toString()
    ).observe(this) {
      when (it) {
        is Resource.Loading -> {
          binding.apply {
            progressBar.show()
            btnLogin.isEnabled = false
          }
        }

        is Resource.Success -> {
          binding.progressBar.hide()
          binding.btnLogin.isEnabled = true
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
            btnLogin.isEnabled = true
          }
          toast(it.message.toString())
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    viewModel.checkUser().observe(this) { loggedIn ->
      if (loggedIn) {
        startActivity(
          Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          }
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}