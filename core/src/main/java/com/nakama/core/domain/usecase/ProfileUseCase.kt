package com.nakama.core.domain.usecase

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface ProfileUseCase {
  fun getUserData(): Flow<Resource<UserModel>>
}