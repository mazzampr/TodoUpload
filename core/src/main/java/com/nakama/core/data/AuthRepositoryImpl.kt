package com.nakama.core.data

import com.nakama.core.data.model.Resource
import com.nakama.core.data.remote.NetworkResponse
import com.nakama.core.data.remote.RemoteDataSource
import com.nakama.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
  private val remoteDataSource: RemoteDataSource
): AuthRepository {
  override fun login(email: String, password: String): Flow<Resource<String>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.login(email, password).first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }

  override fun register(name: String, email: String, password: String): Flow<Resource<String>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.register(name, email, password).first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }

  override fun checkUser(): Flow<Boolean> = flow {
    emit(remoteDataSource.checkUser().first())
  }

}