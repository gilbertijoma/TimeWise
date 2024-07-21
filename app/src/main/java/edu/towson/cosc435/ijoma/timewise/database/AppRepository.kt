package edu.towson.cosc435.ijoma.timewise.sampledata

import android.content.ContentValues
import com.google.firestore.v1.Cursor
import edu.towson.cosc435.ijoma.timewise.database.DatabaseContract
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.database.Note
import edu.towson.cosc435.ijoma.timewise.database.Reminder
import edu.towson.cosc435.ijoma.timewise.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//database methods
class AppRepository(private val dbHelper: DatabaseHelper) {
    //inserts users during signup
    suspend fun insertUser(firstName: String, lastName: String, email: String, password: String) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseContract.UserEntry.COLUMN_FIRST_NAME, firstName)
                put(DatabaseContract.UserEntry.COLUMN_LAST_NAME, lastName)
                put(DatabaseContract.UserEntry.COLUMN_EMAIL, email)
                put(DatabaseContract.UserEntry.COLUMN_PASSWORD, password)
            }
            db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values)
        }

    }

    //suspend fun getReminders(email: String){
    //
    //    val db = dbHelper.readableDatabase
    //
    //    val projection = arrayOf(
    //        DatabaseContract.ReminderEntry.COLUMN_NOTE_ID,
    //        DatabaseContract.ReminderEntry.COLUMN_TITLE,
    //        DatabaseContract.ReminderEntry.COLUMN_PRIORITY_LEVEL,
    //        DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME
    //    )

    //    val selec

    //}
    //gets user data for login
    suspend fun getUserByEmail(email: String): User? {
        val db = dbHelper.readableDatabase
        //all relevant fields selected for operation
        val projection = arrayOf(
            DatabaseContract.UserEntry.COLUMN_USER_ID,
            DatabaseContract.UserEntry.COLUMN_FIRST_NAME,
            DatabaseContract.UserEntry.COLUMN_LAST_NAME,
            DatabaseContract.UserEntry.COLUMN_EMAIL,
            DatabaseContract.UserEntry.COLUMN_PASSWORD
        )
        //query that selects by email
        val selection = "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?"
        val selectionArgs = arrayOf(email)

        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var user: User? = null
        //grabs all relevant columns
        if (cursor.moveToFirst()) {
            val userId =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_USER_ID))
            val firstName =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_FIRST_NAME))
            val lastName =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_LAST_NAME))
            val userEmail =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_EMAIL))
            val password =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_PASSWORD))

            user = User(userId, firstName, lastName, userEmail, password)
        }
        cursor.close()

        return user
    }

    suspend fun getAllUsers(): String {
        withContext(Dispatchers.IO) {

        }
        val db = dbHelper.readableDatabase
        //gets only first and last name
        val projection = arrayOf(
            DatabaseContract.UserEntry.COLUMN_FIRST_NAME,
            DatabaseContract.UserEntry.COLUMN_LAST_NAME
        )
        //selects all users
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val users = StringBuilder()
        with(cursor) {
            while (moveToNext()) {
                val firstName =
                    getString(getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_FIRST_NAME))
                val lastName =
                    getString(getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_LAST_NAME))
                users.append("\n$firstName $lastName")
            }
        }
        cursor.close()
        return users.toString()

    }

    suspend fun deleteReminderByNoteId(noteId: Int) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase

            // Define  the query.
            val selection = "${DatabaseContract.ReminderEntry.COLUMN_NOTE_ID} = ?"
            // Specify arguments
            val selectionArgs = arrayOf(noteId.toString())
            //  delete
            db.delete(DatabaseContract.ReminderEntry.TABLE_NAME, selection, selectionArgs)
        }
    }

    suspend fun deleteRemindersByUserId(userId: String) {
        //when a user logs out / deletes data this function will be called
        //deletes all reminders made by a user
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val selection = "${DatabaseContract.ReminderEntry.COLUMN_USER_ID} LIKE ?"
            val selectionArgs = arrayOf(userId)
            db.delete(DatabaseContract.ReminderEntry.TABLE_NAME, selection, selectionArgs)
        }

    }

    suspend fun insertReminder(
        userId: String,
        email: String,
        title: String,
        text: String,
        priorityLevel: String,
        dateAndTime: String,
        notify: Boolean,
        completed: Boolean,
        emailSent: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase

            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(DatabaseContract.ReminderEntry.COLUMN_USER_ID, userId)
                put(DatabaseContract.ReminderEntry.COLUMN_EMAIL, email)
                put(DatabaseContract.ReminderEntry.COLUMN_TITLE, title)
                put(DatabaseContract.ReminderEntry.COLUMN_TEXT, text)
                put(DatabaseContract.ReminderEntry.COLUMN_PRIORITY_LEVEL, priorityLevel)
                put(DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME, dateAndTime)
                put(DatabaseContract.ReminderEntry.COLUMN_NOTIFY, if (notify) 1 else 0)
                put(DatabaseContract.ReminderEntry.COLUMN_COMPLETED, if (completed) 1 else 0)
                put(DatabaseContract.ReminderEntry.COLUMN_EMAIL_SENT, if (emailSent) 1 else 0)

            }

            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insert(DatabaseContract.ReminderEntry.TABLE_NAME, null, values)
        }


    }

    fun getAllReminders(): List<Reminder> {
        //gets all reminders in the database
        val reminders = mutableListOf<Reminder>()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            DatabaseContract.ReminderEntry.COLUMN_NOTE_ID,
            DatabaseContract.ReminderEntry.COLUMN_USER_ID,
            DatabaseContract.ReminderEntry.COLUMN_EMAIL,
            DatabaseContract.ReminderEntry.COLUMN_TITLE,
            DatabaseContract.ReminderEntry.COLUMN_TEXT,
            DatabaseContract.ReminderEntry.COLUMN_PRIORITY_LEVEL,
            DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME,
            DatabaseContract.ReminderEntry.COLUMN_NOTIFY,
            DatabaseContract.ReminderEntry.COLUMN_COMPLETED,
            DatabaseContract.ReminderEntry.COLUMN_EMAIL_SENT

        )

        val cursor = db.query(
            DatabaseContract.ReminderEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val noteId =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_NOTE_ID))
                val userId =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_USER_ID))
                val email =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_EMAIL))
                val title =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_TITLE))
                val text =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_TEXT))
                val priorityLevel =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_PRIORITY_LEVEL))
                val dateAndTime =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME))
                val notify =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_NOTIFY)) > 0
                val completed =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_COMPLETED)) > 0
                val emailSent =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_EMAIL_SENT)) > 0


                reminders.add(
                    Reminder(
                        noteId,
                        userId,
                        email,
                        title,
                        text,
                        priorityLevel,
                        dateAndTime,
                        notify,
                        completed,
                        emailSent
                    )
                )
            }
            close()
        }

        return reminders
    }

    fun getRemindersByDate(date: String): List<Reminder> {
        //gets all reminders on a particular day
        val reminders = mutableListOf<Reminder>()
        val db = dbHelper.readableDatabase

        val selection = "${DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME} = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query(
            DatabaseContract.ReminderEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val noteId =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_NOTE_ID))
                val userId =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_USER_ID))
                val email =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_EMAIL))
                val title =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_TITLE))
                val text =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_TEXT))
                val priorityLevel =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_PRIORITY_LEVEL))
                val dateAndTime =
                    getString(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME))
                val notify =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_NOTIFY)) > 0
                val completed =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_COMPLETED)) > 0
                val emailSent =
                    getInt(getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_EMAIL_SENT)) > 0

                reminders.add(
                    Reminder(
                        noteId,
                        userId,
                        email,
                        title,
                        text,
                        priorityLevel,
                        dateAndTime,
                        notify,
                        completed,
                        emailSent
                    )
                )
            }
            close()
        }

        return reminders
    }

    fun getReminderByNoteId(noteId: Int): Reminder? {
        //get reminder using primary key
        val db = dbHelper.readableDatabase
        val selection = "${DatabaseContract.ReminderEntry.COLUMN_NOTE_ID} = ?"
        val selectionArgs = arrayOf(noteId.toString())

        val cursor = db.query(
            DatabaseContract.ReminderEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var reminder: Reminder? = null
        if (cursor.moveToFirst()) {
            val userId =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_USER_ID))
            val email =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_EMAIL))
            val title =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_TITLE))
            val text =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_TEXT))
            val priorityLevel =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_PRIORITY_LEVEL))
            val dateAndTime =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_DATE_AND_TIME))
            val notify =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_NOTIFY)) > 0
            val completed =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_COMPLETED)) > 0
            val emailSent =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReminderEntry.COLUMN_EMAIL_SENT)) > 0

            reminder = Reminder(
                noteId,
                userId,
                email,
                title,
                text,
                priorityLevel,
                dateAndTime,
                notify,
                completed,
                emailSent
            )
        }
        cursor.close()
        return reminder
    }

    fun getDueReminders(currentDateTime: String): List<Reminder> {
        //Used specifically for email notifications
        val dueReminders = ArrayList<Reminder>()
        val db = dbHelper.readableDatabase
        //query to search for all reminders that haven't been sent as an email and are in time range
        val query = """
            SELECT * FROM Reminder WHERE emailSent = 0 AND 
            (strftime('%s', dateAndTime) BETWEEN strftime('%s', ?) - 30 * 60 AND strftime('%s', ?))
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(currentDateTime, currentDateTime))

        with(cursor) {
            while (moveToNext()) {
                val reminder = Reminder(
                    getInt(getColumnIndexOrThrow("noteId")),
                    getString(getColumnIndexOrThrow("userId")),
                    getString(getColumnIndexOrThrow("email")),
                    getString(getColumnIndexOrThrow("title")),
                    getString(getColumnIndexOrThrow("text")),
                    getString(getColumnIndexOrThrow("priorityLevel")),
                    getString(getColumnIndexOrThrow("dateAndTime")),
                    getInt(getColumnIndexOrThrow("notify")) > 0,
                    getInt(getColumnIndexOrThrow("completed")) > 0,
                    getInt(getColumnIndexOrThrow("emailSent")) > 0
                )
                dueReminders.add(reminder)
            }
            close()
        }

        return dueReminders
    }
    //updates reminder after notification is sent
    fun updateReminder(noteId: Int, completed: Boolean, emailSent: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put("completed", if (completed) 1 else 0)
        cv.put("emailSent", if (emailSent) 1 else 0)

        val result = db.update("Reminder", cv, "noteId = ?", arrayOf(noteId.toString()))
        db.close()
        return result != -1 // return true if the update was successful
    }

    suspend fun insertNote(name: String, dateCreated: String, text: String, email: String) {
        withContext(Dispatchers.IO) {
            //inserts note into the database
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("name", name)
                put("dateCreated", dateCreated)
                put("text", text)
                put("email", email)
            }
            db.insert("notes", null, values)
        }

    }

    fun updateNote(nId: Int, name: String, text: String): Int {
        //updates note using ID
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("text", text)
        }
        return db.update("notes", values, "nId = ?", arrayOf(nId.toString()))
    }
    fun deleteNote(nId: Int): Int {
        //deletes note using ID
        val db = dbHelper.writableDatabase
        return db.delete("notes", "nId = ?", arrayOf(nId.toString()))
    }
    suspend fun getNotesByEmail(email: String): List<Note> {
        //gets notes by email for UI
        val db = dbHelper.readableDatabase
        val projection = arrayOf("nId", "name", "dateCreated", "text", "email")
        val selection = "email = ?"
        val selectionArgs = arrayOf(email)

        val cursor = db.query(
            "notes",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        //grabs all relevant columns
        val notes = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            val nId = cursor.getInt(cursor.getColumnIndexOrThrow("nId"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val dateCreated = cursor.getString(cursor.getColumnIndexOrThrow("dateCreated"))
            val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
            val noteEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            //puts it into a list to return
            notes.add(Note(nId, name, dateCreated, text, noteEmail))
        }
        cursor.close()

        return notes
    }


    suspend fun getRemindersByEmail(email: String): List<Reminder> {
        //gets reminder by email for UI
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            "noteId", "userId", "email", "title", "text", "priorityLevel",
            "dateAndTime", "notify", "completed", "emailSent"
        )
        val selection = "email = ?"
        val selectionArgs = arrayOf(email)

        val cursor = db.query(
            "Reminder",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        //grabs all relevant columns
        val reminders = mutableListOf<Reminder>()
        while (cursor.moveToNext()) {
            val noteId = cursor.getInt(cursor.getColumnIndexOrThrow("noteId"))
            val userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"))
            val reminderEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
            val priorityLevel = cursor.getString(cursor.getColumnIndexOrThrow("priorityLevel"))
            val dateAndTime = cursor.getString(cursor.getColumnIndexOrThrow("dateAndTime"))
            val notify = cursor.getInt(cursor.getColumnIndexOrThrow("notify")) != 0
            val completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed")) != 0
            val emailSent = cursor.getInt(cursor.getColumnIndexOrThrow("emailSent")) != 0

            //puts it into a list to return
            reminders.add(Reminder(noteId, userId, reminderEmail, title, text, priorityLevel, dateAndTime, notify, completed, emailSent))
        }
        cursor.close()

        return reminders
    }


}