package com.nakama.core.data

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.UserModel
import com.nakama.core.data.remote.NetworkResponse
import com.nakama.core.data.remote.RemoteDataSource
import com.nakama.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl(
  private val remoteDataSource: RemoteDataSource
): ProfileRepository {
  override fun getUserData(): Flow<Resource<UserModel>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.getUserData().first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }
}