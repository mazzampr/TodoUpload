package com.nakama.todoupload.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nakama.core.data.model.Resource
import com.nakama.core.data.model.Task
import com.nakama.core.ui.TaskAdapter
import com.nakama.core.utils.Constants.DONE
import com.nakama.core.utils.Constants.IN_PROGRESS
import com.nakama.core.utils.Constants.TO_DO
import com.nakama.core.utils.cancelNotification
import com.nakama.core.utils.getMimeType
import com.nakama.core.utils.hide
import com.nakama.core.utils.setupRecyclerView
import com.nakama.core.utils.show
import com.nakama.core.utils.toast
import com.nakama.todoupload.databinding.FragmentHomeBinding
import com.nakama.todoupload.utils.showBottomNavView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!
  private val viewModel by viewModel<HomeViewModel>()
  private lateinit var todosAdapter: TaskAdapter
  private lateinit var inProgressAdapter: TaskAdapter
  private lateinit var doneAdapter: TaskAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    showBottomNavView()
    // Inflate the layout for this fragment
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observe()
  }

  private fun observe() {
    viewModel.getAllTasks().observe(viewLifecycleOwner) { resource ->
      when (resource) {
        is Resource.Error -> {
          binding.progressBar.hide()
          toast(resource.message ?: "Terjadi kesalahan")
        }
        is Resource.Loading -> {
          binding.progressBar.show()
        }
        is Resource.Success -> {
          binding.progressBar.hide()
          resource.data?.let {
            initRv()
            setUpDataRv(it)
          }
        }
      }
    }
  }

  private fun initRv() {
    todosAdapter = TaskAdapter(TO_DO)
    inProgressAdapter = TaskAdapter(IN_PROGRESS)
    doneAdapter = TaskAdapter(DONE)

    setupRecyclerView(binding.rvInTodo, todosAdapter) {position ->
      val task = todosAdapter.differ.currentList[position]
      deleteTask(task)
    }
    setupRecyclerView(binding.rvInProgress, inProgressAdapter) {position ->
      val task = inProgressAdapter.differ.currentList[position]
      deleteTask(task)
    }
    setupRecyclerView(binding.rvDone, doneAdapter) {position ->
      val task = doneAdapter.differ.currentList[position]
      deleteTask(task)
    }
  }

  private fun setUpDataRv(task: List<Task>) {
    val todoList = task.filter { it.status == TO_DO }
    todoList.isNotEmpty().let {
      todosAdapter.differ.submitList(todoList)
    }

    val inProgressList = task.filter { it.status == IN_PROGRESS }
    inProgressList.isNotEmpty().let {
      inProgressAdapter.differ.submitList(inProgressList)
    }

    val doneList = task.filter { it.status == DONE }
    doneList.isNotEmpty().let {
      doneAdapter.differ.submitList(doneList)
    }

    setRvActions()
  }

  private fun setRvActions() {
    val listAdapter = listOf(todosAdapter, inProgressAdapter, doneAdapter)
    listAdapter.forEach { adapter ->

      adapter.onDoneClicked = {
        updateTask(it.copy(status = DONE))
        cancelNotification(requireContext(), it.notificationId)
      }

      adapter.onProgressClicked = {
        updateTask(it.copy(status = IN_PROGRESS))
      }

      adapter.btnDeleteClick = {
        deleteTask(it)
      }

      adapter.onFileClick = {
        lifecycleScope.launch {
          val mimeType = getMimeType(it.file!!)
          openFile(it.file!!, mimeType)
        }
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
          observe()
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
          cancelNotification(requireContext(), task.notificationId)
          toast(it.data ?: "")
          observe()
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}