package com.udacity.project4.locationreminders.reminderslist

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.LiveDataTestUtil
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.data.local.FakeDatabase
import com.udacity.project4.locationreminders.data.local.FakeRemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization.IS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var reminderssViewModel: RemindersListViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var remindersDatasource: FakeDataSource
    private lateinit var list : MutableList<ReminderDTO>

    @Before
    fun setupStatisticsViewModel() {
        stopKoin()
        // Initialise the repository with no tasks.
        val reminder = ReminderDTO(title = "title ", description = "desc", location = "loc", latitude = 0.123, longitude =0.123, id = UUID.randomUUID().toString())
        val reminder1 = ReminderDTO(title = "title 1", description = "desc1", location = "loc1", latitude = 0.123, longitude =0.123, id = UUID.randomUUID().toString())
        val reminder2 = ReminderDTO(title = "title 2", description = "desc2", location = "loc2", latitude = 0.123, longitude =0.123, id = UUID.randomUUID().toString())
        list = mutableListOf(reminder,reminder1,reminder2)

        val fakeData = FakeDatabase(list)
        remindersDatasource = FakeDataSource(fakeData)

        reminderssViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),remindersDatasource)
    }




    @Test
    fun getremindersViewVisible() {

//        given

        reminderssViewModel.loadReminders()

        // When get values
        val listofreminders = LiveDataTestUtil.getValue(reminderssViewModel.remindersList)


        // Then its size is the same as items in


        assert(listofreminders.size == 3)
    }
    @Test
    fun getRemindersTestLoading(){
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()
        //        given
        reminderssViewModel.loadReminders()
        // When get values
        assertThat(reminderssViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()
        // Then

        mainCoroutineRule.runBlockingTest {
            assertThat(reminderssViewModel.showLoading.getOrAwaitValue(), `is`(false))
        }




    }

    @Test
    fun getRemindersWithError(){
        //        given
        remindersDatasource.setReturnError(true)
        // When get values
        reminderssViewModel.loadReminders()
        val listofreminders = LiveDataTestUtil.getValue(reminderssViewModel.remindersList)

        // Then
        assertThat(reminderssViewModel.showNoData.getOrAwaitValue(), `is`(true))
        assertThat(reminderssViewModel.showSnackBar.getOrAwaitValue(), `is`("Error in getting data"))




    }



}