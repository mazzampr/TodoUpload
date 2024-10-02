package com.nakama.core.domain.interactor

import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.data.model.UserModel
import com.nakama.core.domain.repository.ProfileRepository
import com.nakama.core.domain.repository.TaskRepository
import com.nakama.core.domain.usecase.ProfileUseCase
import com.nakama.core.domain.usecase.TaskUseCase
import kotlinx.coroutines.flow.Flow
import java.io.File

class TaskInteractor(private val taskRepository: TaskRepository): TaskUseCase {
  override fun getAllTask(): Flow<Resource<List<Task>>> =
    taskRepository.getAllTask()

  override fun createTask(task: Task, file: File): Flow<Resource<String>> =
    taskRepository.createTask(task, file)

  override fun updateTask(task: Task): Flow<Resource<String>> =
    taskRepository.updateTask(task)

  override fun deleteTask(task: Task): Flow<Resource<String>> =
    taskRepository.deleteTask(task)

  override fun getTaskByDate(dueDate: String): Flow<Resource<List<Task>>> =
    taskRepository.getTaskByDate(dueDate)

}