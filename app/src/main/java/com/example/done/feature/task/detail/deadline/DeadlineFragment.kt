package com.example.done.feature.task.detail.deadline

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import com.example.done.R
import com.example.done.common.CalendarConstants
import com.example.done.common.formatDate
import com.example.done.data.calendar.CalendarDay
import com.example.done.data.date.DoneDate
import com.example.done.data.date.TYPE_DEADLINE
import com.example.done.databinding.DialogDeadlineBinding
import com.example.done.feature.calendar.CalendarItem
import com.example.done.feature.calendar.CalendarViewModel
import com.example.done.feature.task.detail.TaskDetailViewModel
import com.example.done.view.CalendarPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeadlineFragment : BottomSheetDialogFragment() {

    private var _binding: DialogDeadlineBinding? = null
    private val binding get() = _binding!!
    lateinit var pager: CalendarPager
    private var selectedMonth: Int = 0
    private var selectedYear: Int = 0
    private lateinit var today: CalendarDay
    private val viewModel: CalendarViewModel by viewModel()
    private val sharedViewModel: TaskDetailViewModel by sharedViewModel()
    private lateinit var rootView: View
    private lateinit var dialog: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var title: TextView
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var min = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogDeadlineBinding.inflate(inflater, container, false)
        rootView = binding.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED


        val translateToRight = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            1.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f
        )

        val translateToLeft = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT,
            1.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f
        )

        translateToRight.duration = 1000
        translateToLeft.duration = 1000
        translateToRight.fillAfter = true
        translateToLeft.fillAfter = true
        translateToRight.interpolator = AccelerateDecelerateInterpolator()
        translateToLeft.interpolator = AccelerateDecelerateInterpolator()

        val time = binding.timePicker
        val date = binding.frameLayout
        title = binding.dialogTitle
        val close = binding.dialogClose
        val next = binding.nextBtn

        time.hour = 9
        time.minute = 0

        time.setOnTimeChangedListener { _, hourOfDay, minute ->
            hour = hourOfDay
            min = minute
        }

        translateToRight.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit

            override fun onAnimationEnd(animation: Animation?) {
                date.translationZ = 0f
            }

            override fun onAnimationRepeat(animation: Animation?) = Unit

        })

        translateToLeft.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                date.translationZ = 2f
            }

            override fun onAnimationEnd(animation: Animation?) = Unit

            override fun onAnimationRepeat(animation: Animation?) = Unit
        })

        val layout: CoordinatorLayout = dialog.findViewById(R.id.bottomSheetLayout)!!

        layout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels


        val include = binding.calendarView

        pager = include.pagerView
        val monthName = include.calendarMonthName
        val adapter = HomeAdapter(childFragmentManager)

        pager.adapter = adapter

        pager.addOnPageChangeListener(object : CalendarPager.OnPageChangeListener() {
            override fun onPageSelected(year: Int, month: Int) {
                selectedYear = year
                selectedMonth = month

                monthName.text = "$year ${CalendarConstants.months[month]}"
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


        next.setOnClickListener {
            if (next.text == resources.getString(R.string.next)) {
                date.startAnimation(translateToRight)
                next.text = resources.getString(R.string.save)
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    sharedViewModel.addDeadline(
                        DoneDate(
                            0,
                            0,
                            TYPE_DEADLINE,
                            year,
                            month,
                            day,
                            hour,
                            min,
                            0
                        )
                    )
                    dismiss()
                }
            }
        }

        close.setOnClickListener {
            dismiss()
        }

    }

    private fun showTranslateAnim() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDayClick(calendarDay: CalendarDay) {
        title.text = formatDate(
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
        year = calendarDay.mYear
        month = calendarDay.mMonth
        day = calendarDay.mDay
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }
}