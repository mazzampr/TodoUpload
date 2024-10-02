package com.nakama.core.domain.interactor

import com.nakama.core.data.model.Resource
import com.nakama.core.domain.repository.AuthRepository
import com.nakama.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthInteractor(private val authRepository: AuthRepository): AuthUseCase {
  override fun login(email: String, password: String): Flow<Resource<String>> =
    authRepository.login(email, password)

  override fun register(name: String, email: String, password: String): Flow<Resource<String>> =
    authRepository.register(name, email, password)

  override fun checkUser(): Flow<Boolean> =
    authRepository.checkUser()
}