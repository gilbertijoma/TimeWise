package edu.towson.cosc435.ijoma.timewise.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

//user class holds user info and login credentials