package com.nakama.todoupload.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.nakama.core.data.model.Task
import com.nakama.core.domain.usecase.TaskUseCase
import java.io.File
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
class TaskViewModel(private val taskUseCase: TaskUseCase) : ViewModel() {
  fun createTask(task: Task, file: File) = taskUseCase.createTask(task, file).asLiveData()
  fun updateTask(task: Task) = taskUseCase.updateTask(task).asLiveData()
  fun deleteTask(task: Task) = taskUseCase.deleteTask(task).asLiveData()
  fun getDetailTaskByDate(dueDate: String) = taskUseCase.getTaskByDate(dueDate).asLiveData()
}