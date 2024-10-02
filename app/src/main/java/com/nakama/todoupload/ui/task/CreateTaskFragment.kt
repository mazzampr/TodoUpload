package com.nakama.todoupload.ui.task

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.notification.NotificationReceiver
import com.nakama.core.utils.hide
import com.nakama.core.utils.show
import com.nakama.core.utils.showDatePicker
import com.nakama.core.utils.toast
import com.nakama.core.utils.uriToFile
import com.nakama.todoupload.databinding.FragmentCreateTaskBinding
import com.nakama.todoupload.utils.hideBottomNavView
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class CreateTaskFragment : Fragment() {
  private var _binding: FragmentCreateTaskBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<TaskViewModel>()
  private var selectedDate: Date? = null
  private var file: File? = null
  private lateinit var dueDateString: String
  private lateinit var timeString: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    hideBottomNavView()
    // Inflate the layout for this fragment
    _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupAction()
  }

  private fun setupAction() {
    binding.apply {
      etDueDate.setOnClickListener {
        showDatePicker(context as AppCompatActivity) { dateString,dueTimeString, date ->
          etDueDate.setText(dateString +" "+ dueTimeString)
          dueDateString = dateString
          timeString = dueTimeString
          selectedDate = date
        }
      }
      cvFile.setOnClickListener {
        PickImageDialog.build(PickSetup())
          .setOnPickResult {
            if (it.error == null) {
              file = uriToFile(it.uri, context?.applicationContext!!)
              ivPreview.setImageURI(it.uri)
              toast("File berhasil dipilih")
            } else {
              toast(it.error.message.toString())
            }
          }

          .setOnPickCancel {
            toast("Canceled")
          }.show(childFragmentManager)
      }

      btnBack.setOnClickListener {
        findNavController().navigateUp()
      }

      btnCreate.setOnClickListener {
        if (inputNotValid()) {
          toast("Harap isi semua bagian")
          return@setOnClickListener
        }

        val task = Task(
          title = etTitle.text.toString(),
          description = etDescription.text.toString(),
          dueDate = dueDateString,
          dueTime = timeString
        )
        createTask(task)
      }
    }
  }


  private fun createTask(task: Task) {
    viewModel.createTask(task, file!!).observe(viewLifecycleOwner) {
      when(it) {
        is Resource.Loading -> {
          binding.apply {
            progressBar.show()
            btnCreate.hide()
          }
        }
        is Resource.Success -> {
          toast(it.data.toString())
          setNotification(task.notificationId)
          findNavController().navigateUp()
        }
        is Resource.Error -> {
          binding.apply {
            progressBar.hide()
            btnCreate.show()
          }
          toast(it.message.toString())
        }
      }
    }
  }

  private fun setNotification(id: Int) {
    selectedDate?.let {
      val notificationDate = it.time - 2 * 60 * 60 * 1000 // Reminder 2 hours (120 min) before the due date
      val intent = Intent(requireContext(), NotificationReceiver::class.java)
      intent.putExtra("title", "Task Reminder")
      intent.putExtra("text", "Due date for task ${binding.etTitle.text.toString()} is in 2 hours")
      val pendingIntent = PendingIntent.getBroadcast(
        requireContext(),
        id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
      alarmManager.set(AlarmManager.RTC_WAKEUP, notificationDate, pendingIntent)
    }
  }

  private fun inputNotValid(): Boolean {
    return binding.etTitle.text.toString().isEmpty() || binding.etDescription.text.toString().isEmpty() ||
        binding.etDueDate.text.toString().isEmpty() || file == null
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}