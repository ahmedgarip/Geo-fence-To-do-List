package com.udacity.project4.locationreminders.data.local

import android.util.Log
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(
    private val remindersDao: RemindersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {
    private var shouldReturnError = false
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> = withContext(ioDispatcher) {
        if (shouldReturnError) {
            return@withContext Result.Error("Error in getting data")
        }


            Result.Success(remindersDao.getReminders())

    }

    override suspend fun saveReminder(reminder: ReminderDTO) =
        withContext(ioDispatcher) {
            remindersDao.saveReminder(reminder)

        }

    override suspend fun getReminder(id: String): Result<ReminderDTO> = withContext(ioDispatcher) {
        if (shouldReturnError) {
            return@withContext Result.Error("Error in getting data")
        }


            val reminder = remindersDao.getReminderById(id)
            if (reminder != null) {
                return@withContext Result.Success(reminder)
            } else {
                return@withContext Result.Error("Reminder not found!")
            }

    }

    override suspend fun deleteAllReminders() {
        withContext(ioDispatcher) {
            remindersDao.deleteAllReminders()
        }
    }

}
class FakeDatabase(var reminders:MutableList<ReminderDTO> = mutableListOf()) :
    RemindersDao {


    override suspend fun getReminders(): List<ReminderDTO> {
        return reminders.toList()
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        reminders.firstOrNull { it.id == reminderId }?.let { return it }
        return null
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}