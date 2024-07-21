package edu.towson.cosc435.ijoma.timewise.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import edu.towson.cosc435.ijoma.timewise.viewmodels.NoteViewModel
import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
import edu.towson.cosc435.ijoma.timewise.popups.createPopup
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import edu.towson.cosc435.ijoma.timewise.ui.theme.Turquoise3
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(nav: NavController, viewModel: ReminderViewModel, noteViewModel: NoteViewModel) {
    /*
    This is the Login Screen for our home application, we pass the two view models and the nav controller.
    We create two text fields for username and passwords and have logic to log the user in if it exists, otherwise
    raise an error saying it doesn't exist. We also have a clickable text field to send users to the sign up.
     */


    //Get the lifecycle for calling the database
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current



    //Handles showing the notification for if the user enters incorrect credentials
    //Defaults to false, but when the user enters invalid credentials, we throw true and raise an error
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value){
        createPopup(
            onDismiss = {showDialog.value=false},
            title = "Login Failed",
            message = "Incorrect Credentials")
    }


    //Handle remembering the outline text fields for the email and password the user types in
    //Username in this scenerio is the email
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }


    //Title of the app
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(140.dp))
        Text("TimeWise", style = MaterialTheme.typography.displayLarge, color = Turquoise3)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){



        Spacer(modifier = Modifier.height(75.dp))

        //Login Text Field asking for the users Email
        OutlinedTextField(value = username.value,

            onValueChange = {newUsername -> username.value = newUsername }, //Update the field as the user types
            label = { Text("Email") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp)



        )

        Spacer(modifier = Modifier.height(10.dp))


        //Password Text Field
        OutlinedTextField(value = password.value,

            //Update the field when the user types
            onValueChange = {newPassword -> password.value = newPassword },
            label = { Text("Password") },
            singleLine = true,

            //Set it so the password is hidden when the user types
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))


        //Login Button For user to click
        Button(onClick = {
            lifecycleOwner.lifecycleScope.launch {

                //Call helper function to check is the credentials entered are correct or not
                //Passes the username and password, and context for database
                //Returns a boolean to see if credentials are correct or not
                val correctLogIn = handleLogIn(username.value, password.value, context)


                if (correctLogIn){
                    //Username/Password match, navigate to home
                    viewModel.username.value = username.value
                    noteViewModel.username.value = username.value
                    nav.navigate("home"){
                        popUpTo("auth"){
                            inclusive = true
                        }
                    }
                }
                else{
                    //Username/Password dont match, raise popup saying invalid credentials
                    showDialog.value = true
                }
            }


        },
            border = BorderStroke(3.dp, Turquoise3),
            colors = ButtonDefaults.buttonColors( containerColor = Color(0,150,152), contentColor = Color.White),
            modifier = Modifier
                .width(275.dp)
                .height(50.dp)

        ) {
            Text("Sign In")

        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {

            //Clickable text for if the user is new, send them to the sign up screen
            ClickableText(text = AnnotatedString("New to the app? Sign Up"),
                onClick = {

                    //Send From login screen to sign up screen
                    nav.navigate("register")
                })
        }


    }

}

//This function will handle interacting with the database
suspend fun handleLogIn(username : String, password : String, context: Context ): Boolean {

    /*
    This first checks if the email(username) is in the database by querying. if it returns null, we pass since it doesnt exist.
    If it returns a value, we check to see if the user we retrieveds password matches the one the user typed in.
    If it is, we return true, in every other situation, we return false.
     */

    //Pass database parameters
    val dbHelper = DatabaseHelper(context)
    val appRepository = AppRepository(dbHelper)

    //make sure to pass in email (change login to only accept email not username)
    //This returns true if email is in database and if password entered matches password in the database
    val result = appRepository.getUserByEmail(username)

    //If username exists
    if (result != null) {
        //Return result of if password equals the password typed
        return result.password == password
    }else{
        //Username doesnt exist, return false
        return false
    }




}