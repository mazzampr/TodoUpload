package com.nakama.core.domain.repository

import com.nakama.core.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
  fun login(email: String, password: String): Flow<Resource<String>>
  fun register(name: String, email: String, password: String): Flow<Resource<String>>
  fun checkUser(): Flow<Boolean>
}