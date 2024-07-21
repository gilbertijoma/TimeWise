package edu.towson.cosc435.ijoma.timewise.database

import android.provider.BaseColumns
//contract class that specifies the layout of the schema
object DatabaseContract {

    object UserEntry : BaseColumns {
        const val TABLE_NAME = "User"
        const val COLUMN_USER_ID = "userId"
        const val COLUMN_FIRST_NAME = "firstName"
        const val COLUMN_LAST_NAME = "lastName"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    object ReminderEntry : BaseColumns {
        const val TABLE_NAME = "Reminder"
        const val COLUMN_NOTE_ID = "noteId"
        const val COLUMN_USER_ID = "userId"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TEXT = "text"
        const val COLUMN_PRIORITY_LEVEL = "priorityLevel"
        const val COLUMN_DATE_AND_TIME = "dateAndTime"
        const val COLUMN_NOTIFY = "notify"
        const val COLUMN_COMPLETED = "completed"
        const val COLUMN_EMAIL_SENT = "emailSent"

    }
}