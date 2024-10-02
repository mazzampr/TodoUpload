package com.nakama.core.utils

import android.R
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nakama.core.notification.NotificationReceiver
import com.nakama.core.utils.Constants.DONE
import com.nakama.core.utils.Constants.IN_PROGRESS
import com.nakama.core.utils.Constants.TO_DO
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun Activity.toast(msg: String) {
  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
  Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun View.show() {
  this.visibility = View.VISIBLE
}

fun View.hide() {
  this.visibility = View.INVISIBLE
}

fun View.gone() {
  this.visibility = View.GONE
}

fun ImageView.glide(url: String) {
  Glide.with(this).load(url).into(this)
}

fun showDatePicker(context: Context, func: (String, String, Date) -> Unit) {
  val calendar = Calendar.getInstance()
  val year = calendar.get(Calendar.YEAR)
  val month = calendar.get(Calendar.MONTH)
  val day = calendar.get(Calendar.DAY_OF_MONTH)
  val hour = calendar.get(Calendar.HOUR_OF_DAY)
  val minute = calendar.get(Calendar.MINUTE)

  var selectedDate: Date

  val datePickerDialog = DatePickerDialog(
    context,
    { _, selectedYear, selectedMonth, selectedDay ->
      val selectedCalendar = Calendar.getInstance()
      selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
      selectedDate = selectedCalendar.time

      TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
          selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
          selectedCalendar.set(Calendar.MINUTE, selectedMinute)
          selectedDate = selectedCalendar.time

          val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
          val dateString = dateFormat.format(selectedDate)
          val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
          val timeString = timeFormat.format(selectedDate)

          func(dateString, timeString, selectedDate)
        },
        hour,
        minute,
        true
      ).show()
    },
    year,
    month,
    day
  )

  datePickerDialog.show()
}

fun dateToString(date: Date): String =
  SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(date)

fun stringToDate(date: String): Date? =
  SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).parse(date)

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateWithFormatAsDate(): Date {
  val currentDate = LocalDate.now()
  val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
  val formattedDateString = currentDate.format(formatter)

  val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
  return dateFormat.parse(formattedDateString)
}


private const val MAXIMAL_SIZE = 1000000 //1 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun drawableToBytes(drawable: Drawable) : ByteArray {
  val bitmap = (drawable as BitmapDrawable).bitmap
  val baos = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
  return baos.toByteArray()
}

fun createCustomTempFile(context: Context): File {
  val filesDir = context.externalCacheDir
  return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun getFileName(uri: Uri, context: Context): String? {
  var result: String? = null
  if (uri.scheme == "content") {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
      if (it.moveToFirst()) {
        result = it.getString(it.getColumnIndexOrThrow("_display_name"))
      }
    }
  }
  if (result == null) {
    result = uri.path
    val cut = result?.lastIndexOf('/')
    if (cut != -1) {
      result = result?.substring(cut!! + 1)
    }
  }
  return result
}

fun uriToFile(imageUri: Uri, context: Context): File {
  val myFile = createCustomTempFile(context)
  val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
  val outputStream = FileOutputStream(myFile)
  val buffer = ByteArray(1024)
  var length: Int
  while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
  outputStream.close()
  inputStream.close()
  return myFile
}

@RequiresApi(Build.VERSION_CODES.Q)
fun File.reduceFileImage(): File {
  val file = this
  val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
  var compressQuality = 100
  var streamLength: Int
  do {
    val bmpStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
    val bmpPicByteArray = bmpStream.toByteArray()
    streamLength = bmpPicByteArray.size
    compressQuality -= 5
  } while (streamLength > MAXIMAL_SIZE)
  bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
  return file
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
  val orientation = ExifInterface(file).getAttributeInt(
    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
  )
  return when (orientation) {
    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
    ExifInterface.ORIENTATION_NORMAL -> this
    else -> this
  }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
  val matrix = Matrix()
  matrix.postRotate(angle)
  return Bitmap.createBitmap(
    source, 0, 0, source.width, source.height, matrix, true
  )
}

fun getStatus(status: String): String {
  return when (status) {
    TO_DO -> "To-Do"
    IN_PROGRESS -> "In Progress"
    DONE -> "Done"
    else -> ""
  }
}


suspend fun getMimeType(url: String): String {
  return withContext(Dispatchers.IO) {
    val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = "HEAD"
    connection.connect()
    connection.contentType ?: ""
  }
}

fun cancelNotification(context: Context, notificationId: Int) {
  val intent = Intent(context, NotificationReceiver::class.java)
  val pendingIntent = PendingIntent.getBroadcast(
    context,
    notificationId,
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
  )
  val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  alarmManager.cancel(pendingIntent)
}

fun setupRecyclerView(
  recyclerView: RecyclerView,
  adapter: RecyclerView.Adapter<*>,
  onDeleteItem: (Int) -> Unit,
) {
  recyclerView.apply {
    layoutManager = LinearLayoutManager(recyclerView.context)
    this.adapter = adapter
  }

  // Set up ItemTouchHelper for swipe-to-delete
  val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
    0, ItemTouchHelper.LEFT
  ) {
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
      return false // We don't want to support moving items
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
      val position = viewHolder.adapterPosition
      onDeleteItem(position)
    }

    override fun onChildDraw(
      c: Canvas,
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      dX: Float,
      dY: Float,
      actionState: Int,
      isCurrentlyActive: Boolean,
    ) {

      RecyclerViewSwipeDecorator.Builder(
        c,
        recyclerView,
        viewHolder,
        dX,
        dY,
        actionState,
        isCurrentlyActive
      )
        .addBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.holo_red_light))
        .addActionIcon(R.drawable.ic_menu_delete)
        .create()
        .decorate()
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
  })

  itemTouchHelper.attachToRecyclerView(recyclerView)
}