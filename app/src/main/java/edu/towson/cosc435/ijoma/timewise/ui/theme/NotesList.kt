package edu.towson.cosc435.ijoma.timewise.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.towson.cosc435.ijoma.timewise.database.UiNote

@Composable
fun NotesList(notesList: List<UiNote>) {
    LazyColumn {
        items(notesList) { note ->
            NotesColumn(
                modifier = Modifier,
                title = note.title,
               description = note.description
            )
        }
    }
}
@Composable
fun NotesColumn(modifier: Modifier, title: String, description: String){
    Card(
        modifier = modifier
            .padding(10.dp)
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Top) {
                Text(
                    text = title,
                        fontSize = 20.sp

                )
                Text(text = description)
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(Turquoise3)

                ) {
                    Text("Edit")
                }
            }
        }
    }
}
