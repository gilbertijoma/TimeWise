package edu.towson.cosc435.ijoma.timewise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import edu.towson.cosc435.ijoma.timewise.database.UiNote
import edu.towson.cosc435.ijoma.timewise.ui.theme.NotesList
import edu.towson.cosc435.ijoma.timewise.ui.theme.Turquoise3


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController) {

    var notes by remember { mutableStateOf(listOf<UiNote>()) }

    var newNoteText by remember { mutableStateOf("") }
    var newNoteTitle by remember { mutableStateOf("") }
    val turquoise = Color(0xFF008080)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(50.dp))
        Text("Notes", style = MaterialTheme.typography.displayLarge, color = Turquoise3)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = newNoteTitle,
            onValueChange = { newNoteTitle = it },
            label = { Text("Title", color = Turquoise3) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = turquoise,
                unfocusedBorderColor = Color.Gray
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),

            )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = newNoteText,
            onValueChange = { newNoteText = it },
            label = { Text("Description",  color = Turquoise3) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = turquoise,
                unfocusedBorderColor = Color.Gray
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (newNoteTitle.isNotEmpty() && newNoteText.isNotEmpty()) {
                    notes += UiNote(newNoteTitle, newNoteText)
                    newNoteTitle = ""
                    newNoteText = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(Turquoise3)
        ) {
            Text("Add Note", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))
        NotesList(notes)
    }
}



