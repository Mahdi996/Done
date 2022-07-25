package com.example.done.feature.calendar

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.done.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarItem : Fragment() {

    private val mYear by lazy { arguments!!.get(EXTRA_YEAR) as Int }
    private val mMonth by lazy { arguments!!.get(EXTRA_MONTH) as Int }
    lateinit var adapter: CalendarAdapter
    val viewModel: CalendarViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_calendar_pager, container, false)
    }

    companion object {

        private const val EXTRA_YEAR = "extra_year"
        private const val EXTRA_MONTH = "extra_month"

        fun newInstance(year: Int, month: Int) = CalendarItem().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_YEAR, year)
                putInt(EXTRA_MONTH, month)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.daysRV)

        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(R.attr.dividerColorDone, typedValue, true)
        val color = typedValue.data

        lifecycleScope.launch(Dispatchers.IO) {

        adapter = CalendarAdapter(
            mYear,
            mMonth,
            viewModel.days(mYear, mMonth),color
        )
            withContext(Dispatchers.Main) {
                rv.adapter = adapter
            }
    }

        rv.layoutManager = GridLayoutManager(
            requireContext(),
            7,
            RecyclerView.VERTICAL,
            false
        )
    }
}