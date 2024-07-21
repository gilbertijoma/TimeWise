package edu.towson.cosc435.ijoma.timewise.database

data class Note(
    val nId: Int,
    val name: String,
    val dateCreated: String,
    val text: String,
    val email: String
)