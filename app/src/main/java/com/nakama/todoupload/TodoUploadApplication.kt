package com.nakama.todoupload

import android.app.Application
import com.google.firebase.FirebaseApp
import com.nakama.core.di.firebaseModule
import com.nakama.core.di.repositoryModule
import com.nakama.todoupload.di.useCaseModule
import com.nakama.todoupload.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TodoUploadApplication: Application() {

  override fun onCreate() {
    super.onCreate()
    FirebaseApp.initializeApp(this)
    startKoin {
      androidLogger(Level.NONE)
      androidContext(this@TodoUploadApplication)
      modules(
        listOf(
          firebaseModule,
          useCaseModule,
          repositoryModule,
          viewModelModule
        )
      )
    }
  }
}