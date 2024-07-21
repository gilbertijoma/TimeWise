package edu.towson.cosc435.ijoma.timewise.popups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
/*
Function for handling creating a generic popup to display to the user when using sign up.
This way, instead of having 3 specific popups, constantly defined, this function created the popups, and the
methodology in the signup just tells the app what  parameters to pass, taking the title of the popup and the error
message we want to display to the user (and ability to dismiss when done)
 */
fun createPopup(
    title : String,
    message : String,
    onDismiss:()->Unit){

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {onDismiss},
        modifier = Modifier.height(180.dp),

        title = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                )
                //title of error
                {
                    Text(text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)
                }

                //Message desciption
                Text(
                    text = message,
                    fontSize = 20.sp)

                //Button to dismiss popup
                Button(
                    onClick = {onDismiss()},
                    modifier = Modifier
                        .width(188.dp)
                        .padding(10.dp)
                ) {
                    Text(text = "Okay")
                }


            }
        },
    )

}
