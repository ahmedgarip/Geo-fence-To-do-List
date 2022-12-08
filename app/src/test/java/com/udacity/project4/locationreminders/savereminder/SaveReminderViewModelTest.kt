package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.MyApp
import com.udacity.project4.locationreminders.LiveDataTestUtil
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.FakeDatabase
import com.udacity.project4.locationreminders.data.local.FakeRemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.hamcrest.core.IsEqual
import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest  {
    private lateinit var dataSource : FakeDatabase
    private lateinit var SaveViewModel : SaveReminderViewModel
    private lateinit var fakeDataSource : FakeDataSource

    private lateinit var reminder :ReminderDTO
    private lateinit var reminder1 :ReminderDataItem
    private lateinit var reminder2:ReminderDataItem
    private lateinit var reminder3 :ReminderDataItem
    private lateinit var reminder4 :ReminderDataItem
    private lateinit var reminder5 :ReminderDataItem


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setupViewModel()=mainCoroutineRule.runBlockingTest {
        stopKoin()
        dataSource = FakeDatabase()
        fakeDataSource = FakeDataSource(dataSource, Dispatchers.Unconfined)

        reminder = ReminderDTO(title = "title ", description = "desc", location = "loc", latitude = 0.123, longitude =0.123)
        reminder1 = ReminderDataItem(title = "title 1", description = "desc1", location = "loc1", latitude = 0.123, longitude =0.123)
        reminder2 = ReminderDataItem(title = "title 2", description = "desc2", location = "loc2", latitude = 0.123, longitude =0.123)
        reminder3 = ReminderDataItem(title = "title 3", description = "desc3", location = "loc3", latitude = 0.123, longitude =0.123)
        reminder4 = ReminderDataItem(title = "title 4", description = "desc4", location = "loc4", latitude = 0.123, longitude =0.123)
        reminder5 = ReminderDataItem(title = "title 5", description = "desc5", location = "loc5", latitude = 0.123, longitude =0.123)



        fakeDataSource.saveReminder(reminder)

        mainCoroutineRule.runBlockingTest{dataSource.saveReminder(reminder)}
        SaveViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }
    @After
    fun clear(){

    }


    //TODO: provide testing to the SaveReminderView and its live data objects
    @Test
    fun saveReminder_addtask()= mainCoroutineRule.runBlockingTest {
//        given
        SaveViewModel.saveReminder(reminder1)
        val thisreminder1 = dataSource.getReminderById(reminder1.id)
        assertThat(thisreminder1?.title).isEqualTo(reminder1.title )

    }

    @Test
    fun testTitleLiveData(){
//        given datd
        SaveViewModel.reminderTitle.value = reminder4.title
//        when
        val value = LiveDataTestUtil.getValue(SaveViewModel.reminderTitle)
//        then
        assert(value == reminder4.title)
    }
    @Test
    fun testDescriptionLiveData(){
//        given datd
        SaveViewModel.reminderDescription.value = reminder2.description
//        when
        val value = LiveDataTestUtil.getValue(SaveViewModel.reminderDescription)
//        then
        assert(value == reminder2.description)
    }

    @Test
    fun testPOILiveData(){
//        given datd
        SaveViewModel.reminderSelectedLocationStr.value = reminder3.location
//        when
        val value = LiveDataTestUtil.getValue(SaveViewModel.reminderSelectedLocationStr)
//        then
        assert(value == reminder3.location)
    }



}


