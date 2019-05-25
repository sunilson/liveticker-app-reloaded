package at.arkulpa.data.local

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.SharedPreferences
import at.arkulpa.data.local.database.daos.AccountDetailsDao
import at.arkulpa.data.local.database.daos.PhotoDao
import at.arkulpa.data.local.database.daos.StepDao
import at.arkulpa.data.local.database.models.DatabaseAccountDetails
import at.arkulpa.data.local.database.models.DatabaseStep
import at.arkulpa.domain.models.AccountDetails
import at.arkulpa.domain.models.Location
import at.arkulpa.domain.models.ReminderSettings
import at.arkulpa.domain.models.Step
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class LocalDataSourceTest {


    @MockK
    lateinit var sharedPreferences: SharedPreferences

    @MockK
    lateinit var photoDao: PhotoDao

    @MockK
    lateinit var stepDao: StepDao

    @MockK
    lateinit var accountDetailsDao: AccountDetailsDao

    @MockK
    lateinit var alarmManager: AlarmManager

    @MockK
    lateinit var alarmIntent: PendingIntent

    @MockK
    lateinit var editor: SharedPreferences.Editor

    private val steps = (1..10).map {
        DatabaseStep(
            "",
            Step("", it, it == 5, 14, 10, Date(), null, null)
        )
    }

    private val accountDetails = DatabaseAccountDetails(
        "",
        AccountDetails(
            "", "", "", "", "", "",
            ReminderSettings("", ""),
            Location("", "", "", "", "", "", "", "")
        )
    )

    private val localDataSource
        get() = LocalDataSource(
            alarmManager,
            alarmIntent,
            sharedPreferences,
            sharedPreferences,
            photoDao,
            accountDetailsDao,
            stepDao
        )

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { editor.putString(any(), any()) } answers { editor }
        every { editor.putBoolean(any(), any()) } answers { editor }
        every { editor.putLong(any(), any()) } an