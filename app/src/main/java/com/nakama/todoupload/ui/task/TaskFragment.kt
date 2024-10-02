package com.nakama.todoupload.ui.task

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.ui.TaskAdapter
import com.nakama.core.ui.TaskDetailAdapter
import com.nakama.core.utils.Constants
import com.nakama.core.utils.cancelNotification
import com.nakama.core.utils.dateToString
import com.nakama.core.utils.getCurrentDateWithFormatAsDate
import com.nakama.core.utils.getMimeType
import com.nakama.core.utils.gone
import com.nakama.core.utils.hide
import com.nakama.core.utils.setupRecyclerView
import com.nakama.core.utils.show
import com.nakama.core.utils.toast
import com.nakama.todoupload.databinding.FragmentTaskBinding
import com.nakama.todoupload.utils.showBottomNavView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class TaskFragment : Fragment() {

  private var _binding: FragmentTaskBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<TaskViewModel>()
  private var selectedDate: Date = getCurrentDateWithFormatAsDate()
  private lateinit var todosDetailAdapter: TaskDetailAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    showBottomNavView()
    // Inflate the layout for this fragment
    _binding = FragmentTaskBinding.inflate(inflater, container, false)
    return binding.root
  }

  @SuppressLint("SetTextI18n")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initRv()
    setupAction()
    getTaskDetailByDate(dateToString(selectedDate))
    binding.currentDateTextView.text = "Task - ${dateToString(selectedDate)}"
  }

  @SuppressLint("SetTextI18n")
  private fun setupAction() {
    binding.apply {
      btnAddTask.setOnClickListener {
        findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToCreateTaskFragment())
      }
      calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDate = calendar.time
        getTaskDetailByDate(dateToString(selectedDate))
        binding.currentDateTextView.text = "Task - ${dateToString(selectedDate)}"
      }
    }
  }

  private fun getTaskDetailByDate(dueDate: String) {
    viewModel.getDetailTaskByDate(dueDate).observe(viewLifecycleOwner) { resource ->
      when (resource) {
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.gone()
          val tasks = resource.data ?: emptyList()
          setUpDataRv(tasks)
        }
        is Resource.Error -> {
          binding.progressBar.gone()
          binding.emptyView.show()
          toast(resource.message.toString())
        }
      }
    }
  }

  private fun initRv() {
    todosDetailAdapter = TaskDetailAdapter()

    setupRecyclerView(binding.rvTask, todosDetailAdapter) {position ->
      val task = todosDetailAdapter.differ.currentList[position]
      deleteTask(task)
    }
  }

  private fun setUpDataRv(tasks: List<Task>) {
    todosDetailAdapter.differ.submitList(tasks)
    binding.emptyView.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
    setRvActions()
  }

  private fun setRvActions() {
    todosDetailAdapter.onDoneClicked = {
      updateTask(it.copy(status = Constants.DONE))
      cancelNotification(requireContext(), it.notificationId)
    }
    todosDetailAdapter.onProgressClicked = {
      updateTask(it.copy(status = Constants.IN_PROGRESS))
    }
    todosDetailAdapter.btnDeleteClick = {
      deleteTask(it)
    }
    todosDetailAdapter.onFileClick = {
      lifecycleScope.launch {
        val mimeType = getMimeType(it.file!!)
        openFile(it.file!!, mimeType)
      }
    }
  }

  private fun openFile(url: String, mimeType: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(url), mimeType)
    startActivity(intent)
  }

  private fun updateTask(task: Task) {
    viewModel.updateTask(task).observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Error -> {
          binding.progressBar.hide()
          toast(it.message ?: "Terjadi kesalahan")
        }
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.hide()
          toast(it.data ?: "")
          getTaskDetailByDate(dateToString(selectedDate))
        }
      }
    }
  }

  private fun deleteTask(task: Task) {
    viewModel.deleteTask(task).observe(viewLifecycleOwner) {
      when (it) {
        is Resource.Error -> {
          binding.progressBar.hide()
          toast(it.message ?: "Terjadi kesalahan")
        }
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.hide()
          toast(it.data ?: "")
          cancelNotification(requireContext(), task.notificationId)
          getTaskDetailByDate(dateToString(selectedDate))
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}