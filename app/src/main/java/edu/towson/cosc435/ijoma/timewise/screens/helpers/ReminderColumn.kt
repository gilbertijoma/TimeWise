package edu.towson.cosc435.ijoma.timewise.screens.helpers

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import edu.towson.cosc435.ijoma.timewise.viewmodels.Note
import edu.towson.cosc435.ijoma.timewise.viewmodels.NoteViewModel
import edu.towson.cosc435.ijoma.timewise.viewmodels.Reminder
import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.ui.theme.Turquoise3
import kotlinx.coroutines.launch


@Composable
fun reminderOverview(
    /*
    This function handles displaying the popup overiview when a user selects one of the reminders from the list.
    Inside, they will have the ability to view each reminders description, along with the ability to delete the reminder if they
    so choose. If I had more time, I would have loved to add a confirmation on the delete.

    This function takes the reminder class in question, along with the ReminderViewModel (So we can call the filter function)
     */
    viewModel: ReminderViewModel,
    reminder: Reminder,
    context: Context,
    onDismiss:()->Unit){

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    //Make a simple popup to display the description
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { /*TODO*/},
        modifier = Modifier.height(450.dp),

        title = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(text = reminder.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text("Description:\n\n${reminder.description}")

                Spacer(modifier = Modifier.height(20.dp))

                //Add a button so the user can back out (they can also back out by clicking off
                Button(
                    onClick = {
                        onDismiss()},
                    modifier = Modifier
                        .width(188.dp)
                        .padding(10.dp)
                ) {
                    Text(text = "Back")
                }

                //Add a button to delete the note
                Button(
                    onClick = {

                        lifecycleOwner.lifecycleScope.launch {

                            //val formatTime = selectedTime.value.substring(0)
                            val dbHelper = DatabaseHelper(context)
                            val appRepository = AppRepository(dbHelper)

                            appRepository.deleteReminderByNoteId(reminder.id.toInt())

                        }
                        viewModel.onDelete(reminder)
                        onDismiss()},
                    modifier = Modifier
                        .width(170.dp)
                ) {
                    Text(text = "Delete")
                }
            }
        },
    )
}

@Composable
fun noteOverview(
    /*
   This function handles displaying the popup overiview when a user selects one of the Notes from the list.
   Inside, they will have the ability to view each notes description, along with the ability to delete the note if they
   so choose.

   I know this is an idential function, I will fully admit I took the lazy route and copied/pased the function, it was
   more convinient in the shortrun. If I had more time, I would combine this with the notes overview.

   This function takes the botes class in question, along with the NotesViewModel (So we can call the filter function)
    */
    viewModel: NoteViewModel,
    note: Note,
    context: Context,
    onDismiss:()->Unit){

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { /*TODO*/},
        modifier = Modifier.height(450.dp),

        title = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(text = note.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text("Description: ${note.description}")

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onDismiss()},
                    modifier = Modifier
                        .width(188.dp)
                        .padding(10.dp)
                ) {
                    Text(text = "Back")
                }
                Button(
                    onClick = {

                        lifecycleOwner.lifecycleScope.launch {

                            //val formatTime = selectedTime.value.substring(0)
                            val dbHelper = DatabaseHelper(context)
                            val appRepository = AppRepository(dbHelper)

                            appRepository.deleteNote(note.id.toInt())

                        }

                        viewModel.onDelete(note)
                        onDismiss()},
                    modifier = Modifier
                        .width(170.dp)
                ) {
                    Text(text = "Delete")
                }
            }
        },
    )
}


@Composable
fun noteColumn(
    
    /*
    This function creates the little card for each note the user has,
    
    In it we display the title of the note, and a button they can click that opens an overview for them to delete the note
    or look at its description
     */
    note: Note,
    modifier: Modifier,
    title: String,
    context: Context,
    viewModel: NoteViewModel
){

    //Handles when we show the oveview popup for each note 
    val showNoteOverview = remember { mutableStateOf(false) }
    if (showNoteOverview.value){
        noteOverview(
            viewModel,
            note,
            context,
            onDismiss = {showNoteOverview.value=false}
        )
    }
    
    //Make the card the note goes on
    Card(
        modifier
            .padding(10.dp)
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(10.dp),
    )
    {
        Row(
            modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            
            //Add the Title of what the note is called
            Column(
                modifier = modifier.padding(12.dp)){
                Text(text = title)
            }
            Column(modifier.padding(20.dp)){
                
                //Add a more button that displays the note overview popup
                Button(onClick = {
                                 showNoteOverview.value = true
                                 //viewModel.onDelete(note)
                },
                    border = BorderStroke(3.dp, Turquoise3),
                    colors = ButtonDefaults.buttonColors( containerColor = Color(0,150,152), contentColor = Color.White),
                    modifier = Modifier
                        .width(100.dp)
                        .height(50.dp)

                ) {
                    //Text of button
                    Text("More")
                }
            }

        }
    }
}

@Composable
fun reminderColumn(
    /*
    This function creates the little card for each reminder the user has,
    
    In it we display the title of the reminder, the priority, and the date of the reminder, and a button they can click that opens an overview for them to delete the note
    or look at its description
     */
    
    reminder: Reminder,
    modifier: Modifier,
    title: String,
    priority: String,
    date: String,
    viewModel: ReminderViewModel,
    context: Context
){

    
    //handle remembering when we open up the overview of the reminder
    val showReminderOverview = remember { mutableStateOf(false) }
    if (showReminderOverview.value){
        
        //Call the reminder function
        reminderOverview(
            viewModel,
            reminder,
            context,
            onDismiss = {showReminderOverview.value=false}
        )
    }

    //Make the layout of the reminder card
    Card(
        modifier
            .padding(10.dp)
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(10.dp),
    )
    {
        Row(
            modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        
        //Display the title, date, and priority of the reminder
        {
            Column(modifier.padding(12.dp)){
                Text(text = title)
                Text(text = "Date: $date")
                Text(text = "Priority: $priority")
            }
            
            //Add a button that sets the reminder overview to true.
            Column(modifier.padding(20.dp)){
                Button(onClick = {
                                 showReminderOverview.value = true
                                 //viewModel.onDelete(reminder)
                },

                    //Design the button
                    border = BorderStroke(3.dp, Turquoise3),
                    colors = ButtonDefaults.buttonColors( containerColor = Color(0,150,152), contentColor = Color.White),
                    modifier = Modifier
                        .width(100.dp)
                        .height(50.dp)

                ) {

                    //Text of the button
                    Text("More")
                }
            }

        }
    }
}