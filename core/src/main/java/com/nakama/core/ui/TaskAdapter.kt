package com.nakama.core.ui

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nakama.core.R
import com.nakama.core.data.model.Task
import com.nakama.core.databinding.ItemTaskBinding
import com.nakama.core.utils.Constants.DONE
import com.nakama.core.utils.Constants.IN_PROGRESS
import com.nakama.core.utils.gone
import com.nakama.core.utils.show

class TaskAdapter(private val status: String, private var fromList: Boolean = false): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
  inner class TaskViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    fun bind(task: Task) {
      binding.apply {
        tvTitle.text = task.title
        tvDueDate.text = task.dueDate + " " + task.dueTime
        tvDescription.text = task.description
        cardStatusTask.setCardBackgroundColor(
          when(task.status) {
            DONE -> itemView.context.getColor(R.color.green)
            IN_PROGRESS -> itemView.context.getColor(R.color.yellow)
            else -> itemView.context.getColor(R.color.red)
          }
        )
        tvOrderStatus.text = task.status

        val buttons = listOf(
          itemView,
          btnExpand
        )

        buttons.forEach {
          it.setOnClickListener {
            if (layoutExpand.isVisible) layoutExpand.gone()
            else layoutExpand.show()
          }
        }

        when (status) {
          IN_PROGRESS -> { btnInProgress.isEnabled = false }
          DONE -> { btnDone.isEnabled = false }
        }

        btnInProgress.setOnClickListener {
          onProgressClicked?.invoke(task)
        }
        btnDone.setOnClickListener {
          onDoneClicked?.invoke(task)
        }
        etFile.setOnClickListener {
          onFileClick?.invoke(task)
        }
        btnDelete.setOnClickListener {
          btnDeleteClick?.invoke(task)
        }
      }
    }
  }

  private val diffUtil = object : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
      oldItem.id == newItem.id && oldItem.status == newItem.status

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
      oldItem == newItem
  }

  val differ = AsyncListDiffer(this, diffUtil)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
    TaskViewHolder(
      ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

  override fun getItemCount(): Int =
    if (fromList) differ.currentList.size
    else if (differ.currentList.size > 3) 3
    else differ.currentList.size

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
    val task = differ.currentList[position]
    holder.bind(task)
  }

  var onProgressClicked: ((Task) -> Unit)? = null
  var onDoneClicked: ((Task) -> Unit)? = null
  var onFileClick: ((Task) -> Unit)? = null
  var btnDeleteClick: ((Task) -> Unit)? = null
}