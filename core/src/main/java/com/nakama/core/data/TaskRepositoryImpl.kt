package com.nakama.core.data

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.data.model.UserModel
import com.nakama.core.data.remote.NetworkResponse
import com.nakama.core.data.remote.RemoteDataSource
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File

class TaskRepositoryImpl(
  private val remoteDataSource: RemoteDataSource
): TaskRepository {
  override fun getAllTask(): Flow<Resource<List<Task>>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.getAllTask().first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }

  override fun createTask(task: Task, file: File): Flow<Resource<String>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.createTaskWithFile(task, file).first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }

  override fun updateTask(task: Task): Flow<Resource<String>> = flow{
    emit(Resource.Loading())
    when(val response = remoteDataSource.updateTask(task).first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }

  override fun deleteTask(task: Task): Flow<Resource<String>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.deleteTask(task).first()) {
      is NetworkResponse.Success -> {
        emit(Resource.Success(response.data))
      }
      is NetworkResponse.Error -> {
        emit(Resource.Error(response.errorMessage))
      }
      else -> {}
    }
  }

  override fun getTaskByDate(dueDate: String): Flow<Resource<List<Task>>> = flow {
    emit(Resource.Loading())
    when(val response = remoteDataSource.getTaskByDate(dueDate).first()) {
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