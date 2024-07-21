package edu.towson.cosc435.ijoma.timewise.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ReminderViewModel: ViewModel() {

    //We initialize the data we want to throw in
    //Since its arbitrary we can just do it in the view model

    val username: MutableState<String> = mutableStateOf("")

    //mutableItems handles the list of data class objects
    val mutableReminders: MutableState<List<Reminder>> = mutableStateOf(emptyList())

    fun onAdd(id: String, name: String, description: String, priority: String, date: String, time: String){

        mutableReminders.value = mutableReminders.value.toMutableList().apply {
            add(
                Reminder(
                id = id,
                //id = (mutableReminders.value.size+1).toString(),
                name = name,
                description = description,
                priority = priority,
                date = date,
                time = time,
                isSelected = false
            )
            )
        }
    }

    //Handles the mass deletion of data class objects whos isSelected is true
    fun onDelete(reminder: Reminder){
        println("ID is $reminder.id")
        println("List of reminders before ${mutableReminders.value}")
        mutableReminders.value = mutableReminders.value.filter {it.id != reminder.id}
        println("List of reminders now ${mutableReminders.value}")
    }


    //Handles changing the isSelected value of our data class object
}