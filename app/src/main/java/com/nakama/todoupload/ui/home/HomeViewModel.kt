package com.nakama.todoupload.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.nakama.core.data.model.Task
import com.nakama.core.domain.usecase.TaskUseCase

class HomeViewModel(private val taskUseCase: TaskUseCase): ViewModel() {
  fun getAllTasks() = taskUseCase.getAllTask().asLiveData()
  fun updateTask(task: Task) = taskUseCase.updateTask(task).asLiveData()
  fun deleteTask(task: Task) = taskUseCase.deleteTask(task).asLiveData()
}