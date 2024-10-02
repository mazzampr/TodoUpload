package com.nakama.core.domain.interactor

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.UserModel
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.usecase.ProfileUseCase
import kotlinx.coroutines.flow.Flow

class ProfileInteractor(private val profileRepository: ProfileRepository): ProfileUseCase {
  override fun getUserData(): Flow<Resource<UserModel>> =
    profileRepository.getUserData()

}