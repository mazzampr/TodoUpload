package com.nakama.todoupload.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.material.textfield.TextInputEditText
import com.nakama.core.domain.usecase.AuthUseCase

class AuthViewModel(private val authUseCase: AuthUseCase): ViewModel() {
  fun login(email: String, password: String) =
    authUseCase.login(email, password).asLiveData()

  fun register(name: String, email: String, password: String) =
    authUseCase.register(name, email, password).asLiveData()

  fun checkUser() = authUseCase.checkUser().asLiveData()

  fun validateLoginForm(emailInput: TextInputEditText, passwordInput: TextInputEditText): Pair<Boolean, String> {
    val email = emailInput.text.toString()
    val password = passwordInput.text.toString()
    val errorMsg: String

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      emailInput.error = "Invalid email"
      return Pair(false, "") // Invalid email
    }

    if (email.isEmpty() || password.isEmpty()) {
      errorMsg = "Please fill all fields"
      return Pair(false, errorMsg) // Empty fields
    }

    return Pair(true, "") // Validation successful
  }

  fun validateRegisterForm(nameInput: TextInputEditText, emailInput: TextInputEditText, passwordInput: TextInputEditText) : Pair<Boolean, String> {
    val name = nameInput.text.toString()
    val email = emailInput.text.toString()
    val password = passwordInput.text.toString()
    val errorMsg: String

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      emailInput.error = "Invalid email"
      return Pair(false, "") // Invalid email
    }

    if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
      errorMsg = "Please fill all fields"
      return Pair(false, errorMsg)
    }

    return Pair(true, "")
  }

}