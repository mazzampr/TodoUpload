package com.nakama.core.domain.usecase

import com.nakama.core.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
  fun login(email: String, password: String): Flow<Resource<String>>
  fun register(name: String, email: String, password: String): Flow<Resource<String>>
  fun checkUser(): Flow<Boolean>
}