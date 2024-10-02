package com.nakama.core.domain.repository

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProfileRepository {
  fun getUserData(): Flow<Resource<UserModel>>
  fun editProfilePic(user: UserModel, file: File): Flow<Resource<String>>
}