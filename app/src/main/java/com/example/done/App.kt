package com.example.done

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import androidx.room.Room
import com.example.done.common.AppDatabase
import com.example.done.data.calendar.CalendarRepository
import com.example.done.data.calendar.CalendarRepositoryImpl
import com.example.done.data.setting.SettingRepository
import com.example.done.data.setting.SettingRepositoryImpl
import com.example.done.data.subtask.SubTaskRepository
import com.example.done.data.subtask.SubTaskRepositoryImpl
import com.example.done.data.task.TaskRepository
import com.example.done.data.task.TaskRepositoryImpl
import com.example.done.feature.calendar.CalendarViewModel
import com.example.done.feature.profile.ProfileViewModel
import com.example.done.feature.task.detail.TaskDetailViewModel
import com.example.done.feature.task.list.TaskViewModel
import ir.smartlab.persindatepicker.util.PersianCalendar
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val myModules = module {
            single {
                Room.databaseBuilder(
                    this@App,
                    AppDatabase::class.java,
                    "fgbjofgpbjposdfgjbsrt"
                ).build()
            }

            factory<TaskRepository> {
                TaskRepositoryImpl(
                    get<AppDatabase>().tasksDao()
                )
            }

            factory<SharedPreferences> {
                this@App.getSharedPreferences(
                    "done_app_settings",
                    MODE_PRIVATE
                )
            }

            factory<SettingRepository> { SettingRepositoryImpl(get()) }

            factory { get<AppDatabase>().dateDao() }

            factory<SubTaskRepository> {
                SubTaskRepositoryImpl(
                    get<AppDatabase>().subTasksDao()
                )
            }

            factory {
                PersianCalendar().apply {
                    set(Calendar.HOUR_OF_DAY, 12)
                }
            }

            factory<CalendarRepository> { CalendarRepositoryImpl(get()) }

            viewModel { TaskViewModel(get(), get(), get()) }
            viewModel { (bundle: Bundle) -> TaskDetailViewModel(bundle, get(), get(), get()) }
            viewModel { CalendarViewModel(get(), get(), get()) }
            viewModel { ProfileViewModel() }


        }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(myModules)
        }

        val settingRepository: SettingRepository = get()
        settingRepository.loadSetting()
    }
}