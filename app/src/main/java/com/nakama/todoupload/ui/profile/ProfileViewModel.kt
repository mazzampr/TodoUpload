package com.nakama.todoupload.ui.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.nakama.core.data.model.UserModel
import com.nakama.core.domain.usecase.AuthUseCase
import com.nakama.core.domain.usecase.ProfileUseCase
import java.io.File

class ProfileViewModel(private val profileUseCase: ProfileUseCase, private val auth: FirebaseAuth): ViewModel() {
  fun getUserData() = profileUseCase.getUserData().asLiveData()
  fun logOut() = auth.signOut()
  fun editProfilePic(user: UserModel, file: File) = profileUseCase.editProfilePic(user, file).asLiveData()
}