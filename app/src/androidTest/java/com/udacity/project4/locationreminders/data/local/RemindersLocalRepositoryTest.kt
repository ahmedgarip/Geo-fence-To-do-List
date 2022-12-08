package com.udacity.project4.locationreminders.data.local

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.android.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest  {

    private val reminder = ReminderDTO(title = "title ", description = "desc", location = "loc", latitude = 0.123, longitude =0.123)
    private val reminder1 = ReminderDTO(title = "title 1", description = "desc1", location = "loc1", latitude = 0.123, longitude =0.123)
    private val reminder2 = ReminderDTO(title = "title 2", description = "desc2", location = "loc2", latitude = 0.123, longitude =0.123)
    private val reminder3 = ReminderDTO(title = "title 3", description = "desc3", location = "loc3", latitude = 0.123, longitude =0.123)
    private val reminder4 = ReminderDTO(title = "title 4", description = "desc4", location = "loc4", latitude = 0.123, longitude =0.123)
    private val reminder5 = ReminderDataItem(title = "title 5", description = "desc5", location = "loc5", latitude = 0.123, longitude =0.123)
    private val newreminders: List<ReminderDTO> = listOf(reminder3,reminder4).sortedBy { it.id }

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var dataSource : RemindersDatabase
    private lateinit var repo : RemindersLocalRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createRepo()=runBlockingTest{

//        dataSource = LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext())
        dataSource = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = RemindersLocalRepository(dataSource.reminderDao(),Dispatchers.Main)

        repo.saveReminder(reminder3)
        repo.saveReminder(reminder4)

    }
    @After
    fun clear()=runBlockingTest{
        repo.deleteAllReminders()

    }


    @Test
    fun getAllReminders_requestAllReminserdsFromDtatSource()=runBlockingTest{
//        when reminders requested from repo
        val reminders = repo.getReminders()
//        then all reminders loaded from datasource

        val remindersData = reminders as Result.Success
        assertThat(remindersData.data.size ,IsEqual(2))


    }
    @Test
    fun saveReminder_test_if_saved()= runBlockingTest{
//        when reminder requested from repo
        repo.saveReminder(reminder1)
        val savedReminder = repo.getReminder(reminder1.id)
//        then  reminder loaded from datasource
        val reminderDatat = savedReminder as Result.Success

        assertThat(reminderDatat.data.id , IsEqual (reminder1.id))
        assertThat(reminderDatat.data.title , IsEqual (reminder1.title))
        assertThat(reminderDatat.data.description , IsEqual (reminder1.description))
        assertThat(reminderDatat.data.location , IsEqual (reminder1.location))
        assertThat(reminderDatat.data.latitude , IsEqual (reminder1.latitude))
        assertThat(reminderDatat.data.longitude , IsEqual (reminder1.longitude))

    }
    @Test
    fun getNotFoundData()= runBlockingTest{
//        when reminder requested from repo

        val reminder = repo.getReminder(reminder2.id)
//        val reminderData = reminder as Result.Success
//        then  error loaded from datasource
        assert(reminder is Result.Error)

        assertThat(reminder.toString() , containsString("Reminder not found!"))

    }


    @Test
    fun deleteAllReminders_deletall_test()=mainCoroutineRule.runBlockingTest {
        //        when reminders deleted
       repo.deleteAllReminders()
        val reminders = repo.getReminders()
//        then all reminders are empty
        val remindersData = reminders as Result.Success

        assertThat(remindersData.data.size ,IsEqual(0))
    }










}