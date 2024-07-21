package edu.towson.cosc435.ijoma.timewise

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.database.Reminder
import kotlinx.coroutines.coroutineScope
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//Define the Retrofit Interface
interface SendGridService {
    @POST("mail/send")
    fun sendEmail(
        @Header("Authorization") authorization: String,
        @Body email: SendGridEmail
    ): Call<Void>
}

data class SendGridEmail(
    //structure for email
    val personalizations: List<Personalization>,
    val from: EmailAddress,
    val subject: String,
    val content: List<Content>
)

data class Personalization(
    val to: List<EmailAddress>
)

data class EmailAddress(
    val email: String
)

data class Content(
    val type: String = "text/plain",
    val value: String
)
//Create a Retrofit Instance
object RetrofitClient {
    val sendGridService: SendGridService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.sendgrid.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SendGridService::class.java)
    }
}


class EmailSenderWorker(
    //initialize worker to send emails periodically based on time stamps
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val databaseHelper = DatabaseHelper(applicationContext)
            val appRepository = AppRepository(databaseHelper)
            //use local time to get all reminders that are due
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dueReminders = appRepository.getDueReminders(currentDateTime.format(formatter))
            Log.d("Send Email Test", "Due Reminders: $dueReminders")
            // For each reminder, send an email and update database
            dueReminders.forEach { reminder ->
                sendEmail(reminder)
                val update = appRepository.updateReminder(reminder.noteId, reminder.completed, true)

            }

            Result.success()
        } catch (e: Exception) {
            Log.d("Send Email Test", "fail")
            e.printStackTrace()
            Result.failure()
        }
    }



private fun sendEmail(reminder: Reminder): Boolean {
    Log.d("Send Email Test", "Entered send email function")
    // Use Retrofit client to send the email with SendGrid API
    // Return true if email is sent successfully, otherwise false
    // Construct the email data object

    val emailData = SendGridEmail(
        personalizations = listOf(
            Personalization(to = listOf(EmailAddress(reminder.email)))
        ),
        from = EmailAddress("timewise.notifications@gmail.com"),
        subject = "TimeWise Reminder: ${reminder.title}",
        content = listOf(Content(value = "You have received an email notification for your reminder:\n${reminder.text}")),

    )

    // Try to send the email using the Retrofit

    val response: Response<Void> = try {
        Log.d("Send Email Test", "Try block executed")
        RetrofitClient.sendGridService.sendEmail(
            "Bearer SG. api key ", // Replace with SendGrid API key
            emailData
        ).execute()
    } catch (e: Exception) {
        //stops potential crash from bad network connectivity or API error
        e.printStackTrace()
        return false
    }
    return if (response.isSuccessful) {

        true
    } else {
        val errorBody = response.errorBody()?.string()

        Log.e("SendEmailTest", "HTTP error: ${response.code()} Body: $errorBody")
        false
    }




}
}