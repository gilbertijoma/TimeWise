package edu.towson.cosc435.ijoma.timewise.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import edu.towson.cosc435.ijoma.timewise.viewmodels.NoteViewModel
import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
import edu.towson.cosc435.ijoma.timewise.popups.addNotePopup
import edu.towson.cosc435.ijoma.timewise.popups.addReminderPopup
import edu.towson.cosc435.ijoma.timewise.screens.helpers.noteColumn
import edu.towson.cosc435.ijoma.timewise.screens.helpers.reminderColumn
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.database.UiNote
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import edu.towson.cosc435.ijoma.timewise.ui.theme.NotesList
import edu.towson.cosc435.ijoma.timewise.ui.theme.Turquoise3
import kotlinx.coroutines.launch


@Composable

fun AppDashboard(
    navController: NavController,
    viewModel: ReminderViewModel,
    noteViewModel: NoteViewModel,
    context: Context
)
/*
The main Dashboard of the Application, and holds most of the apps functionality
This screen displays two lazy columns to the user, one for their reminders,
another for their notes. Each Reminder/Note in the lazy column displays some information, and has a "more"
button that allows you to delete it.

//Each column has a button that open a popup to create a new reminder or note. Inside this popup, you
can select the name, priority, date, etc. and click add, which will automatically add the reminder to the database


This function takes the nav controller for nagivation back, the view model for reminders and notes to
keep track of the reminders/notes displayed, and context for interacting with the database
 */
{

    //Lifecycel for the database
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    //Handles whether to display the add reminder popup to the user
    val showAddReminder = remember { mutableStateOf(false) }
    if (showAddReminder.value){

        //Calls the add Reminder popup, which holds the functionality for the popup
        addReminderPopup(
            viewModel,
            context
        ) { showAddReminder.value = false } // Hide the popup when done
    }

    //Handles whether to display the add note popup to the user
    val showAddNote = remember { mutableStateOf(false) }
    if (showAddNote.value){

        //Calls the add note popup, which holdes the functionality for the popup
        addNotePopup(
            noteViewModel,
            context,
            onDismiss = {showAddNote.value=false} //Hide the popup when done
        )
    }


    val notesList = remember { mutableStateListOf<UiNote>() }
    //val reminderColumn = remember { mutableStateListOf<Reminder>()}
    val reminderCount = 10
    val noteCount = 10
    val rViewModel = ReminderViewModel()


//    val showDialog = remember { mutableStateOf(false) }
//    val title = remember { mutableStateOf("") }
//    val description = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Time Dashboard",  style = MaterialTheme.typography.displayMedium, color = Turquoise3)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your Reminders", style = MaterialTheme.typography.displaySmall, color = Turquoise3)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.height(150.dp)
        ) {


            //We want to grab the list of all users note and reminders now
            lifecycleOwner.lifecycleScope.launch {
                //Establish Database Connection Strings
                val dbHelper = DatabaseHelper(context)
                val appRepository = AppRepository(dbHelper)

                //Grab Result
                val reminderResult = appRepository.getRemindersByEmail(viewModel.username.value)
                val noteResult = appRepository.getNotesByEmail(viewModel.username.value)

                //Empty previous list for added consistency, just grab the new list from the database
                viewModel.mutableReminders.value = emptyList()
                noteViewModel.mutableNotes.value = emptyList()

                    //Add each reminder in the database to the new view model
                for (reminder in reminderResult){

                    val date = reminder.dateAndTime.substring(0, 10)
                    val time = reminder.dateAndTime.substring(11, 19)
                    viewModel.onAdd(reminder.noteId.toString(),reminder.title, reminder.text, reminder.priorityLevel, date, time)
                }

                for (note in noteResult){
                    noteViewModel.onAdd(note.name, note.text)
                }



            }


            //For each reminder in the view model, display it in a lazy list
            itemsIndexed(viewModel.mutableReminders.value){idx, item ->

                //Handles creating each box for each reminder
                reminderColumn(
                    reminder = item,
                    modifier = Modifier,
                    title = item.name,
                    priority = item.priority,
                    date = item.date,
                    viewModel,
                    context
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        //Create a button for adding new reminders
        Button(
            onClick = {

                //Display the popup to the user
                      showAddReminder.value = true
                      //navController.navigate("Remind")
            },
            modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(Turquoise3)

        ) {
            Icon(Icons.Default.Add, contentDescription = "+ Add New ",)
            Text(text = "New Reminder")
       }


        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your Notes", style = MaterialTheme.typography.displaySmall, color = Turquoise3)
        Spacer(modifier = Modifier.height(16.dp))

        //Create a list of all notes the user has in the note view model, and display it to the user in its own special box
        LazyColumn(
            modifier = Modifier.height(150.dp)
        ) {
            itemsIndexed(noteViewModel.mutableNotes.value){idx, item ->
                //Creates the note box
                noteColumn(
                    note = item,
                    modifier = Modifier,
                    title = item.name,
                    context = context,
                    noteViewModel
                )
            }
        }
        NotesList(notesList)

        //Create a button to allow for new notes to be added
        Button(
            onClick = {

                //display the popup to the user
                showAddNote.value = true
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(Turquoise3)

        ) {

            //Text of button
            Icon(Icons.Default.Add, contentDescription = "+ Add New ",)
            Text(text = "New note")
        }
    }
}