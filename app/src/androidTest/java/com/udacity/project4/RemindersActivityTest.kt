package com.udacity.project4

import android.app.Application
import android.view.WindowManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.android.material.internal.ContextUtils.getActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.data.local.FakeDatabase
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import java.util.*


@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test


    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private  var mActivityTestRule= ActivityTestRule(RemindersActivity::class.java)

    private val reminder1 = ReminderDTO(title = "Reminder title 1", description = "Reminder describtion 1", location = "Reminder location 1", latitude = 0.123, longitude =0.123, id = UUID.randomUUID().toString())
    private val reminder2 = ReminderDTO(title = "Reminder title 2", description = "Reminder describtion 2", location = " location 2", latitude = 0.123, longitude =0.123, id = UUID.randomUUID().toString())

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { FakeDataSource(get()) as ReminderDataSource }
            single { FakeDatabase() as RemindersDao }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()


        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
//            repository.saveReminder(reminder1)
        }
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()
    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


//    TODO: add End to End testing to the app
//   ###### the problem was in testing toast after snackbar always fail but when separating
//   ###### the 2 tests it pass so i make 2 test one for toast other for snackbar
//   ###### with testing every funtion alone
@Test
fun testforToast() = runBlocking {
    // Set initial state.
    repository.saveReminder(reminder1)
    repository.saveReminder(reminder2)

    // Start up Tasks screen.
    val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
    dataBindingIdlingResource.monitorActivity(activityScenario)

    mActivityTestRule = ActivityTestRule(RemindersActivity::class.java)




    // Espresso code will go here.

    onView(withId(R.id.reminderssRecyclerView))
        .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        .check(matches(hasDescendant(withText(reminder1.title))))


    Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())

    Espresso.onView(ViewMatchers.withId(R.id.reminderTitle)).perform(typeText("Reminder title 3"))
    Espresso.onView(ViewMatchers.withId(R.id.reminderDescription)).perform(typeText("Reminder Saved description 3"))
    Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(ViewActions.click())
    Espresso.onView(ViewMatchers.withId(R.id.map)).perform(ViewActions.longClick())
    Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())



//  add .check to check the toast 
    Espresso.onView(ViewMatchers.withText(startsWith("Reminder Saved")))
        .inRoot(withDecorView(not(getActivity(appContext)?.getWindow()?.getDecorView())))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    Espresso.onView(ViewMatchers.withText(R.string.reminder_saved))
//        .inRoot(RootMatchers.withDecorView(CoreMatchers.not(getActivity(appContext)?.window?.decorView)))
//        .check(
//            ViewAssertions.matches(ViewMatchers.isDisplayed())
//        )

//    Espresso.onView(ViewMatchers.withId(R.id.reminderTitle)).perform(typeText("Reminder title 4"))
//    Espresso.onView(ViewMatchers.withId(R.id.reminderDescription)).perform(typeText("Reminder description 4"))
////    Espresso.onView(ViewMatchers.withId(R.id.selectedLocation)).perform(typeText("Reminder Saved location 1"))
//    Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(ViewActions.click())
//    Espresso.onView(ViewMatchers.withId(R.id.map)).perform(ViewActions.longClick())
//    Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())






    // Make sure the activity is closed before resetting the db:
    activityScenario.close()
}
    @Test
    fun tsetforsnackbar() = runBlocking {
        // Set initial state.
        repository.saveReminder(reminder1)

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Espresso code will go here.

        onView(withId(R.id.reminderssRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(reminder1.title))))


        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_enter_title)))
        Espresso.onView(ViewMatchers.withId(R.id.reminderTitle)).perform(typeText("my reminder"))
        Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.map)).perform(ViewActions.longClick())
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())







        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }
}

