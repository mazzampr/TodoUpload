package com.nakama.core.data.model

import com.nakama.core.utils.Constants.TO_DO
import java.util.Random
import java.util.UUID

data class Task(
  val id: String = UUID.randomUUID().toString(),
  val notificationId: Int = Random().nextInt(1000),
  val title: String? = null,
  val description: String? = null,
  val dueDate: String? = null,
  val dueTime: String? = null,
  val status: String = TO_DO,
  val file: String? = null
)
