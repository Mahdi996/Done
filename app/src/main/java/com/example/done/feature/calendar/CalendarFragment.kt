package com.example.done.feature.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.done.*
import com.example.done.common.*
import com.example.done.data.calendar.CalendarDay
import com.example.done.data.date.DoneDate
import com.example.done.data.task.Task
import com.example.done.databinding.FragmentCalendarBinding
import com.example.done.feature.task.detail.TaskDetailActivity
import com.example.done.feature.task.list.TaskAdapter
import com.example.done.view.CalendarPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CalendarFragment : Fragment(), TaskAdapter.TaskOnClickListener {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    lateinit var pager: CalendarPager
    private var selectedMonth: Int = 0
    private var selectedYear: Int = 0
    private lateinit var today: CalendarDay
    private val viewModel: CalendarViewModel by viewModel()
    private lateinit var jalali: TextView
    private lateinit var miladi: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val include = binding.calendarIncludeView

        pager = include.pagerView
        val monthName = include.calendarMonthName
        val adapter = HomeAdapter(childFragmentManager)
        jalali = binding.calendarSelectedDayJalali
        miladi = binding.calendarSelectedDay
        pager.adapter = adapter

        pager.addOnPageChangeListener(object : CalendarPager.OnPageChangeListener() {
            override fun onPageSelected(year: Int, month: Int) {
                selectedYear = year
                selectedMonth = month

                monthName.text = "$year ${Constants.months[month]}"
            }
        })

        today = viewModel.getToday()
        selectedYear = today.mYear
        selectedMonth = today.mMonth

        pager.isRtL = true
        pager.setCurrentItem(selectedYear, selectedMonth)

        include.calendarLeftBtn.setOnClickListener {
            pager.loadRightItem()
        }

        include.calendarRightBtn.setOnClickListener {
            pager.loadLeftItem()
        }

        include.calendarTodayBtn.setOnClickListener {
            pager.setCurrentItem(today.mYear, today.mMonth)
        }

        return root
    }

    inner class HomeAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            Log.i("TAG", "position: $position")
            val (year, month) = pager.getYearAndMonth(position)
            Log.i("TAG", "getItem: $year , $month")
            return CalendarItem.newInstance(year, month)
        }

        override fun getCount() = Int.MAX_VALUE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDayClick(calendarDay: CalendarDay) {
        jalali.text = formatDate(
            DoneDate(
                0,
                0,
                0,
                calendarDay.mYear,
                calendarDay.mMonth,
                calendarDay.mDay,
                0,
                0,
                0
            )
        )
        miladi.text = formatDateGregorian(
            DoneDate(
                0,
                0,
                0,
                calendarDay.mYear,
                calendarDay.mMonth,
                calendarDay.mDay,
                0,
                0,
                0
            )
        )
        Log.i("TAG", "onDayClick: ${miladi.text.toString()}")
        viewModel.getOneDayTasks(calendarDay.mYear, calendarDay.mMonth, calendarDay.mDay)
            .observe(this) {
                viewModel.tasks(it).observe(this) { tasks ->
                    val rv = binding.calendarTaskListRv
                    rv.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    val adapter = TaskAdapter(tasks as MutableList<Task>, this)
                    rv.adapter = adapter
                }
            }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemChange(task: Task) {
        //Edit Checkbox or Importance
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.editTask(task)
        }
    }

    override fun onItemClick(task: Task) {
        startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, task)
        })
    }
}