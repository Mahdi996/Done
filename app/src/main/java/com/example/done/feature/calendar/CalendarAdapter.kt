package com.example.done.feature.calendar

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.done.R
import com.example.done.data.calendar.CalendarDay
import com.example.done.view.SquareContainer
import org.greenrobot.eventbus.EventBus

class CalendarAdapter(val year: Int, val month: Int, days: List<CalendarDay>, var color: Int) :
    RecyclerView.Adapter<CalendarAdapter.DayHolder>() {

    val calendarDays = ArrayList<CalendarDay>(days)

    inner class DayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mDayNo = itemView.findViewById<TextView>(R.id.calendar_day_no)
        private val haveTask = itemView.findViewById<View>(R.id.calendar_events)
        private val background = itemView.findViewById<SquareContainer>(R.id.calendar_background)
        private val event = itemView.findViewById<View>(R.id.calendar_events)

        init {
            itemView.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
                if (isFocused) view.performClick()
            }

            itemView.setOnClickListener {
                val selectedDay = calendarDays[adapterPosition]
                EventBus.getDefault().post(calendarDays[adapterPosition])
            }
        }

        fun setupView(day: CalendarDay) {
            itemView.isEnabled = day.isCurrentMonth

            mDayNo.text = day.mDay.toString()

            if (day.haveTask)
                event.visibility = View.VISIBLE

            if (!day.isCurrentMonth)
                mDayNo.setTextColor(color)

            if (day.isToday) {
                background.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.item_calendar_today
                    )
                )
                EventBus.getDefault().post(calendarDays[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_cell, parent, false)

        return DayHolder(view)
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        holder.setupView(calendarDays[position])
    }

    override fun getItemCount() = calendarDays.size

    fun refresh(newDays: List<CalendarDay>) {
        calendarDays.clear()
        calendarDays.addAll(newDays)

        notifyDataSetChanged()
    }
}