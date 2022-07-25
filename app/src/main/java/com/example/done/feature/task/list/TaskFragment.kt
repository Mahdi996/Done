package com.example.done.feature.task.list

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.done.R
import com.example.done.common.EXTRA_KEY_DATA
import com.example.done.data.setting.SettingContainer.hide
import com.example.done.data.setting.SettingContainer.sort
import com.example.done.data.task.Task
import com.example.done.databinding.FragmentHomeBinding
import com.example.done.feature.task.detail.TaskDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskFragment : Fragment(), TaskAdapter.TaskOnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.hideCompleted.value = hide
        when (sort) {
            R.id.showByName -> {
                viewModel.sort.value = SortTask.BY_NAME
            }
            R.id.showByDateAsc -> {
                viewModel.sort.value = SortTask.BY_DATE_ASC
            }
        }

        val rv = binding.tasksRv
        val fab = binding.include.addTaskFab
        val title = binding.include.addTaskED

        val search = binding.homeSearchEd
        val more = binding.homeMoreIv

        search.doOnTextChanged { text, _, _, _ ->
            viewModel.search.value = text.toString()
        }

        //show fab when somethings write in title editText
        title.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty())
                fab.show()
            else
                fab.hide()
        }

        more.setOnClickListener { view ->
            val pop = PopupMenu(requireContext(), view)
            pop.menuInflater.inflate(R.menu.tasks_menu, pop.menu)
            pop.menu.findItem(R.id.hideCompleted).isChecked = hide
            pop.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.hideCompleted -> {
                        saveSettings(!hide, sort)
                        viewModel.hideCompleted.value = !hide
                    }
                    R.id.deleteCompleted -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.deleteCompleted()
                        }
                    }
                    R.id.sortBy -> {
                        pop.menu.findItem(sort).isChecked = true
                    }
                    R.id.showByName -> {
                        saveSettings(hide, R.id.showByName)
                        viewModel.sort.value = SortTask.BY_NAME
                    }
                    R.id.showByDateAsc -> {
                        saveSettings(hide, R.id.showByDateAsc)
                        viewModel.sort.value = SortTask.BY_DATE_ASC
                    }
                }

                return@setOnMenuItemClickListener true
            }
            pop.show()
        }
        val typedValue = TypedValue()
        val theme: Resources.Theme = requireContext().theme
        theme.resolveAttribute(R.attr.dividerColorDone, typedValue, true)
        val color = typedValue.data

        //get List Tasks and Create RecyclerView
        rv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewModel.tasks.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                rv.adapter = TaskAdapter(it as MutableList<Task>, this, color)
            }
        }

        //Add Task
        fab.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.add(title.text.toString())
                view?.hideKeyboard()
                withContext(Dispatchers.Main) {
                    title.text!!.clear()
                }
            }
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    //when check task or importance
    override fun onItemChange(task: Task) {
        Log.i("TAG", "onImportanceClick: ")
        //Edit Checkbox or Importance
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.edit(task)
        }
    }

    //open activity for edit task detail
    override fun onItemClick(task: Task) {

        startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, task)
        })
    }


    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun saveSettings(hide: Boolean, sort: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.saveSettings(hide, sort)
        }
    }
}
