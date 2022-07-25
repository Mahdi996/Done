package com.example.done.feature.task.list

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.done.R
import com.example.done.data.task.Task
import com.google.android.material.card.MaterialCardView

class TaskAdapter(
    var tasks: MutableList<Task>,
    var taskOnClickListener: TaskOnClickListener, var color: Int = 0
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.taskTitle)
        private val importance = itemView.findViewById<ImageView>(R.id.taskImportance)
        private val isChecked = itemView.findViewById<CheckBox>(R.id.taskCheck)
        private val cardView = itemView.findViewById<MaterialCardView>(R.id.itemTaskCardView)

        fun bind(task: Task) {
            title.text = task.title
            if (task.importance)
                importance.setImageResource(R.drawable.ic_round_star_24)
            else
                importance.setImageResource(R.drawable.ic_round_star_border_24)

            isChecked.isChecked = task.isChecked
            if (task.isChecked) {
                title.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                cardView.setCardBackgroundColor(color)
            }

            importance.setOnClickListener {
                task.importance = !task.importance
                taskOnClickListener.onItemChange(task)
            }

            isChecked.setOnClickListener {
                task.isChecked = !task.isChecked
                taskOnClickListener.onItemChange(task)
            }

            title.setOnClickListener {
                taskOnClickListener.onItemClick(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    interface TaskOnClickListener {
        fun onItemChange(task: Task)
        fun onItemClick(task: Task)
    }

}

