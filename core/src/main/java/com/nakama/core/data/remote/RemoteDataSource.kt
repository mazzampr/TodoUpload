package com.nakama.core.data.remote

import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nakama.core.R
import com.nakama.core.data.model.Gallery
import com.nakama.core.data.model.Task
import com.nakama.core.data.model.UserModel
import com.nakama.core.utils.Constants.GALLERY_COLLECTION
import com.nakama.core.utils.Constants.TASK_COLLECTION
import com.nakama.core.utils.Constants.USER_COLLECTION
import com.nakama.core.utils.drawableToBytes
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.net.URI
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RemoteDataSource(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
  private val storage: FirebaseStorage,
) {
  fun login(email: String, password: String): Flow<NetworkResponse<String>> =
    callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
          .addOnFailureListener {
            trySend(NetworkResponse.Error("Something went wrong"))
          }
          .addOnSuccessListener {
            trySend(NetworkResponse.Success("Login Success"))
          }
        awaitClose()
      }

  fun register(name: String, email: String, password: String): Flow<NetworkResponse<String>> =
    callbackFlow {
      auth.createUserWithEmailAndPassword(email, password)
        .addOnFailureListener {
          trySend(NetworkResponse.Error("Something went wrong"))
        }
        .addOnSuccessListener {
          it.user?.let {user->
            val userModel = UserModel(user.uid, name, email)
            firestore.collection(USER_COLLECTION).document(userModel.id)
              .set(userModel)
              .addOnSuccessListener {
                trySend(NetworkResponse.Success("User registered successfully"))
              }
              .addOnFailureListener { e ->
                trySend(NetworkResponse.Error("Failed to create user document: ${e.message}"))
              }
          }
        }
      awaitClose()
    }

  fun checkUser(): Flow<Boolean> = flow{
    emit(auth.currentUser != null)
  }

  fun getUserData(): Flow<NetworkResponse<UserModel>> =
    callbackFlow {
        val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!)
          .addSnapshotListener{value, error ->
            if (error != null) {
              trySend(NetworkResponse.Error("Something went wrong"))
              return@addSnapshotListener
            }
            value?.let {
              val userModel = it.toObject(UserModel::class.java)
              if (userModel != null) {
                trySend(NetworkResponse.Success(userModel))
              }
            }
    }
      awaitClose { listenerRegistration.remove() } }

  fun getAllTask() : Flow<NetworkResponse<List<Task>>> =
    callbackFlow {
      val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!)
        .collection(TASK_COLLECTION)
        .addSnapshotListener { value, error ->
          if (error != null) {
            trySend(NetworkResponse.Error(error.toString()))
            return@addSnapshotListener
          }

          value?.toObjects(Task::class.java)?.let {
            if (it.isEmpty()) {
              trySend(NetworkResponse.Success(it))
            } else {
              trySend(NetworkResponse.Success(it))
            }
          }
        }

      awaitClose { listenerRegistration.remove() }
    }

  private suspend fun createTaskCollection(task: Task): NetworkResponse<String> =
    suspendCoroutine {continuation->
      firestore.collection(USER_COLLECTION).document(auth.uid!!)
        .collection(TASK_COLLECTION)
        .document(task.id)
        .set(task)
        .addOnFailureListener {
          it.printStackTrace()
          continuation.resume(NetworkResponse.Error("Terjadi kesalahan pada server"))
        }
        .addOnSuccessListener {
          continuation.resume(NetworkResponse.Success("Berhasil menambah task"))
        }
    }

  suspend fun createTaskWithFile(task: Task, file: File): Flow<NetworkResponse<String>> = flow {
    try {
      val docByteArray = file.readBytes()
      val imageStorage = storage.reference.child("user/${auth.uid}/task/${task.id}")
      val result = imageStorage.putBytes(docByteArray).await()
      val downloadUrl = result.storage.downloadUrl.await().toString()
      val response = createTaskCollection(task.copy(file = downloadUrl))
      emit(response)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun updateTask(task: Task): Flow<NetworkResponse<String>> =
    callbackFlow {
      firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(TASK_COLLECTION)
        .document(task.id).set(task)
        .addOnFailureListener {
          trySend(NetworkResponse.Error("Terjadi kesalahan"))
        }
        .addOnSuccessListener {
          trySend(NetworkResponse.Success("Status berhasil diubah"))
        }
      awaitClose()
    }

  fun deleteTask(task: Task): Flow<NetworkResponse<String>> =
    callbackFlow {
      firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(TASK_COLLECTION)
        .document(task.id).delete()
        .addOnFailureListener {
          trySend(NetworkResponse.Error("Terjadi kesalahan"))
        }
        .addOnSuccessListener {
          trySend(NetworkResponse.Success("Task berhasil dihapus"))
        }
      awaitClose {  }
    }

  fun getTaskByDate(dueDate: String) : Flow<NetworkResponse<List<Task>>> =
    callbackFlow {
      val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!)
        .collection(TASK_COLLECTION)
        .whereEqualTo("dueDate", dueDate)
        .addSnapshotListener { value, error ->
          if (error != null) {
            trySend(NetworkResponse.Error("Terjadi kesalahan saat mengambil data"))
            return@addSnapshotListener
          }

          value?.toObjects(Task::class.java)?.let {
            if (it.isEmpty()) {
              trySend(NetworkResponse.Success(it))
            } else {
              trySend(NetworkResponse.Success(it))
            }
          }
        }
      awaitClose { listenerRegistration.remove() }
    }

  private suspend fun createGalleryCollection(gallery: Gallery): NetworkResponse<String> =
    suspendCoroutine {continuation->
      firestore.collection(USER_COLLECTION).document(auth.uid!!)
        .collection(GALLERY_COLLECTION)
        .document(gallery.id)
        .set(gallery)
        .addOnFailureListener {
          it.printStackTrace()
          continuation.resume(NetworkResponse.Error("Terjadi kesalahan pada server"))
        }
        .addOnSuccessListener {
          continuation.resume(NetworkResponse.Success("Berhasil menambah media!"))
        }
    }

  suspend fun addGalleryWithFile(gallery: Gallery, file: File): Flow<NetworkResponse<String>> = flow {
    try {
      val docByteArray = file.readBytes()
      val imageStorage = storage.reference.child("user/${auth.uid}/gallery/${gallery.id}")
      val result = imageStorage.putBytes(docByteArray).await()
      val downloadUrl = result.storage.downloadUrl.await().toString()
      val response = createGalleryCollection(gallery.copy(file = downloadUrl))
      emit(response)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun getUserGallery() : Flow<NetworkResponse<List<Gallery>>> =
    callbackFlow {
      val listenerRegistration = firestore.collection(USER_COLLECTION).document(auth.uid!!)
        .collection(GALLERY_COLLECTION)
        .addSnapshotListener { value, error ->
          if (error != null) {
            trySend(NetworkResponse.Error(error.toString()))
            return@addSnapshotListener
          }

          value?.toObjects(Gallery::class.java)?.let {
            if (it.isEmpty()) {
              trySend(NetworkResponse.Empty)
            } else {
              trySend(NetworkResponse.Success(it))
            }
          }
        }

      awaitClose { listenerRegistration.remove() }
    }

  fun deleteGallery(gallery: Gallery): Flow<NetworkResponse<String>> =
    callbackFlow {
      firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(GALLERY_COLLECTION)
        .document(gallery.id).delete()
        .addOnFailureListener {
          trySend(NetworkResponse.Error("Terjadi kesalahan"))
        }
        .addOnSuccessListener {
          trySend(NetworkResponse.Success("Media berhasil dihapus"))
        }
      awaitClose {  }
    }
}