package com.nakama.core.data

import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.data.model.UserModel
import com.nakama.core.data.remote.NetworkResponse
import com.nakama.core.data.remote.RemoteDataSource
import com.nakama.core.domain.repository.GalleryRepository
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File

class GalleryRepositoryImpl(
  private val remoteDataSource: RemoteDataSource
): GalleryRepository {
  override fun getAllUserGallery(): Flow<Resource<List<Gallery>>> = flow {
    emit(Resource.Loading())
    when (val response = remoteDataSource.getUserGallery().first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }

      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }

      is NetworkResponse.Empty -> {
        emit(Resource.Success(emptyList()))
      }
    }
  }

  override fun addGallery(gallery: Gallery, file: File): Flow<Resource<String>> = flow {
    emit(Resource.Loading())
    when (val response = remoteDataSource.addGalleryWithFile(gallery, file).first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }

      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }

      else -> {}
    }
  }

  override fun deleteGallery(gallery: Gallery): Flow<Resource<String>> = flow {
    emit(Resource.Loading())
    when (val response = remoteDataSource.deleteGallery(gallery).first()) {
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