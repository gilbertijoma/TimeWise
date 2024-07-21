package edu.towson.cosc435.ijoma.timewise.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import edu.towson.cosc435.ijoma.timewise.viewmodels.NoteViewModel
import kotlinx.coroutines.launch

@Composable
fun addNotePopup(

    /*
    This function handles the popup for adding notes to the users account. Inside, users
    can type in a title of the note and a description, along with a button that will handle adding it
    to the database. This function takes the NoteViewModel for interacting with the database and adding
    to the list of notes displayed to the user, the context needed for interacting with the database, and the
    dismiss handler for closing the popup when the user is done.
     */

    viewModel: NoteViewModel,
    context: Context,
    onDismiss:()->Unit){


    //handle remembering the title and description of the note
    val noteTitle = remember { mutableStateOf("") }
    val noteDesc = remember { mutableStateOf("") }


    //Lifecycle for handling the interaction with the database
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current


    AlertDialog(

        //We use an alert dialog for displaying the popup, dismiss when done
        onDismissRequest = onDismiss,
        confirmButton = { /*TODO*/},
        modifier = Modifier.height(470.dp),

        title = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){

                    //Title of the popup
                    Text(text = "Add Note",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))

                //Text field for remembering the title of the note the user writes
                OutlinedTextField(value = noteTitle.value,
                    onValueChange = {newNoteTitle -> noteTitle.value = newNoteTitle },
                    label = { Text("Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                //Text field for remembering the description of the note the user writes
                OutlinedTextField(value = noteDesc.value,
                    onValueChange = {newNoteDesc -> noteDesc.value = newNoteDesc },
                    label = { Text("Description") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                //Button for adding the note to the database
                //Previously, each note had a time, but the idea was scrapped, so we just pass a dummy
                //value for the time being for the current time
                Button(
                    onClick = {

                        lifecycleOwner.lifecycleScope.launch {
                            //launch the database and insert the note
                            val dbHelper = DatabaseHelper(context)
                            val appRepository = AppRepository(dbHelper)


                            appRepository.insertNote(noteTitle.value, "NA-NA-NA NA:NA:NA", noteDesc.value, viewModel.username.value)

                        }

                        //Call the on add function to add it to the list displayed to the user and to dismiss the popup
                        viewModel.onAdd(name = noteTitle.value, description = noteDesc.value)
                        onDismiss()},
                    modifier = Modifier
                        .width(188.dp)
                        .padding(10.dp)
                ) {
                    Text(text = "Add")
                }
            }
        },
    )

}
