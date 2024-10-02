package com.nakama.core.domain.usecase

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface TaskUseCase {
  fun getAllTask(): Flow<Resource<List<Task>>>
  fun createTask(task: Task, file: File): Flow<Resource<String>>
  fun updateTask(task: Task): Flow<Resource<String>>
  fun deleteTask(task: Task): Flow<Resource<String>>
  fun getTaskByDate(dueDate: String): Flow<Resource<List<Task>>>
}