package edu.towson.cosc435.ijoma.timewise.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import edu.towson.cosc435.ijoma.timewise.database.DatabaseContract.UserEntry
import edu.towson.cosc435.ijoma.timewise.database.DatabaseContract.ReminderEntry

//helper class that extends SQLiteOpenHelper and manages database creation and version management.
//To use it, initiate the helper and specify a writable or readable database.
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //creates user table
        val createUserTable = """
            CREATE TABLE ${UserEntry.TABLE_NAME} (
                ${UserEntry.COLUMN_USER_ID} INTEGER PRIMARY KEY,
                ${UserEntry.COLUMN_FIRST_NAME} TEXT,
                ${UserEntry.COLUMN_LAST_NAME} TEXT,
                ${UserEntry.COLUMN_EMAIL} TEXT,
                ${UserEntry.COLUMN_PASSWORD} TEXT
            )
        """.trimIndent()
        //creates reminder table
                val createReminderTable = """
            CREATE TABLE ${ReminderEntry.TABLE_NAME} (
                ${ReminderEntry.COLUMN_NOTE_ID} INTEGER PRIMARY KEY,
                ${ReminderEntry.COLUMN_USER_ID} INTEGER,
                ${ReminderEntry.COLUMN_EMAIL} TEXT,
                ${ReminderEntry.COLUMN_TITLE} TEXT,
                ${ReminderEntry.COLUMN_TEXT} TEXT,
                ${ReminderEntry.COLUMN_PRIORITY_LEVEL} TEXT,
                ${ReminderEntry.COLUMN_DATE_AND_TIME} TEXT,
                ${ReminderEntry.COLUMN_NOTIFY} INTEGER,
                ${ReminderEntry.COLUMN_COMPLETED} INTEGER,
                ${ReminderEntry.COLUMN_EMAIL_SENT} INTEGER,
                FOREIGN KEY(${ReminderEntry.COLUMN_USER_ID}) REFERENCES ${UserEntry.TABLE_NAME}(${UserEntry.COLUMN_USER_ID})
            )
        """.trimIndent()
        //creates notes table
         val createNotesTable = """
        CREATE TABLE notes (
            nId INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            dateCreated TEXT NOT NULL,
            text TEXT NOT NULL,
            email TEXT NOT NULL
        )
    """.trimIndent()

             //call functions
        db.execSQL(createUserTable)
        db.execSQL(createReminderTable)
        db.execSQL(createNotesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UserEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ReminderEntry.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 3 // Updated version to handle schema changes
        const val DATABASE_NAME = "MyDatabase.db"
    }
}