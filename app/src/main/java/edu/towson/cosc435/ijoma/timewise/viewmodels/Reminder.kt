package edu.towson.cosc435.ijoma.timewise.viewmodels

class Reminder(
    val id: String,
    val name: String,
    var description: String,
    var priority: String,
    var date: String,
    var time: String,
    val isSelected: Boolean,
){
}