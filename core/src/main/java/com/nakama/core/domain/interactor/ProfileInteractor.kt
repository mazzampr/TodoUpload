package com.nakama.core.domain.interactor

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.UserModel
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.usecase.ProfileUseCase
import kotlinx.coroutines.flow.Flow
import java.io.File

class ProfileInteractor(private val profileRepository: ProfileRepository): ProfileUseCase {
  override fun getUserData(): Flow<Resource<UserModel>> =
    profileRepository.getUserData()

  override fun editProfilePic(user: UserModel, file: File): Flow<Resource<String>> =
    profileRepository.editProfilePic(user, file)

}