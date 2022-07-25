package com.example.done.feature.task.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.done.R
import com.example.done.data.subtask.SubTask

class SubTasksInDetailAdapter(
    private val subs: List<SubTask>,
    private val subTasksAdapterOnClickListener: SubTasksAdapterOnClickListener
) :
    RecyclerView.Adapter<SubTasksInDetailAdapter.SubTasksInDetailViewHolder>() {

    inner class SubTasksInDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val check = itemView.findViewById<CheckBox>(R.id.itemSubTaskCheckbox)
        private val title = itemView.findViewById<TextView>(R.id.itemSubTitle)
        private val clear = itemView.findViewById<ImageView>(R.id.itemSubClear)

        fun bind(sub: SubTask) {

            check.isChecked = sub.isChecked
            title.text = sub.title

            title.setOnClickListener {
                subTasksAdapterOnClickListener.onItemClick(sub)
            }

            clear.setOnClickListener {
                subTasksAdapterOnClickListener.onItemDelete(sub)
                notifyItemChanged(adapterPosition)
            }

            check.setOnClickListener {
                sub.isChecked = !sub.isChecked
                subTasksAdapterOnClickListener.onItemChange(sub)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTasksInDetailViewHolder =
        SubTasksInDetailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sub_task, parent, false)
        )

    override fun onBindViewHolder(holder: SubTasksInDetailViewHolder, position: Int) {
        holder.bind(subs[position])
    }

    override fun getItemCount(): Int = subs.size

    interface SubTasksAdapterOnClickListener {
        fun onItemChange(subTask: SubTask)
        fun onItemClick(subTask: SubTask)
        fun onItemDelete(subTask: SubTask)
    }
}