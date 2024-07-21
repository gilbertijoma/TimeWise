package edu.towson.cosc435.ijoma.timewise.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey val noteId: Int,
    val userId: String,
    val email: String,
    val title: String,
    val text: String,
    val priorityLevel: String, // low, med, high
    val dateAndTime: String, //for app and email notifications
    val notify: Boolean,
    val completed: Boolean,
    val emailSent: Boolean
)

