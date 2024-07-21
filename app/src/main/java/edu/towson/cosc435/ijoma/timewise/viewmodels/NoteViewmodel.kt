package edu.towson.cosc435.ijoma.timewise.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NoteViewModel: ViewModel() {

    //Keep track of the email of the user
    val username: MutableState<String> = mutableStateOf("")

    //mutableItems handles the list of data class objects
    val mutableNotes: MutableState<List<Note>> = mutableStateOf(emptyList())

    fun onAdd(name: String, description: String){
        mutableNotes.value = mutableNotes.value.toMutableList().apply {
            add(
                Note(
                id = mutableNotes.value.size.toString()+1,
                name = name,
                description = description,
                isSelected = false
            )
            )
        }
    }

    //Handles the mass deletion of data class objects whos isSelected is true
    fun onDelete(note: Note){
        println("List of reminders before ${mutableNotes.value}")
        mutableNotes.value = mutableNotes.value.filter {it.id != note.id}
        println("List of reminders now ${mutableNotes.value}")
    }


    //Handles changing the isSelected value of our data class object
}