package com.nakama.core.data.model

import com.nakama.core.utils.Constants.TO_DO
import java.util.UUID

data class Gallery(
  val id: String = UUID.randomUUID().toString(),
  val title: String? = null,
  val file: String? = null
)
