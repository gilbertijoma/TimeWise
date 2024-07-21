package edu.towson.cosc435.ijoma.timewise.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
//import edu.towson.cosc435.ijoma.timewise.sampledata.GMailSender
import edu.towson.cosc435.ijoma.timewise.ui.theme.Turquoise3
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import edu.towson.cosc435.ijoma.timewise.viewmodels.NoteViewModel
import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
import edu.towson.cosc435.ijoma.timewise.popups.createPopup
import edu.towson.cosc435.ijoma.timewise.database.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable

fun SignUpScreen(nav: NavController, viewModel: ReminderViewModel, noteViewModel: NoteViewModel){

    /*
    Handles displaying the sign up screen to the user. This includes displaying an outlined text field for
    the users email, name, password, and retype password. There will be a sign up button that handles functionality
    for logging in, and clickable text that allows users to go back to the log in screen.

    Parameters:
        - NavController - For controlling where the app goes next
        - viewModel - For keeping track of the users username
        - noteViewModel - For consistency
     */

    //initiate context and dbHelper for the database
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val appRepository = AppRepository(dbHelper)


    //mutable states for remembering the outlined text fields
    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val retypePassword = remember { mutableStateOf("") }


    //Handles when to display the popups for when the user clicks sign up and theres an error with the signup process
    val showDialog = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }
    val showDialog3 = remember { mutableStateOf(false) }


    //Creates popups for different error messages, stays hidden when sign up button is not clicked
    if (showDialog.value){
        createPopup(
            onDismiss = {showDialog.value=false},
            title = "Sign Up Failed",
            message = "Email Already Taken")
    }
    if (showDialog2.value){
        createPopup(
            onDismiss = {showDialog2.value=false},
            title = "Sign Up Failed",
            message = "Passwords Dont Match")
    }
    if (showDialog3.value){
        createPopup(
            onDismiss = {showDialog3.value=false},
            title = "Sign Up Failed",
            message = "Please enter a valid email")
    }


    //Sign up title for screen
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("Sign Up", style = MaterialTheme.typography.displayLarge, color = Turquoise3)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){


        //Text Field for Email
        OutlinedTextField(value = email.value,
            onValueChange = {newEmail-> email.value = newEmail },
            label = { Text("Email") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        //Text Field for First Name
        OutlinedTextField(value = username.value,
            onValueChange = {newUsername -> username.value = newUsername },
            label = { Text("First Name") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        //Text Field for Password, ensure password field is hidden when typed
        OutlinedTextField(value = password.value,
            onValueChange = {newPassword -> password.value = newPassword },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))


        //Text field for confirming password
        OutlinedTextField(value = retypePassword.value,
            onValueChange = {newPassword -> retypePassword.value = newPassword },
            label = { Text("Re-type Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(), //keep password hidden when typed
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))


        //Sign up button, handles multiple case scenerios for signing up
        Button(onClick = {

            //Connects to the database and attempts to grab the email from database from users
            var result: User? = null
            lifecycleOwner.lifecycleScope.launch {
                result = appRepository.getUserByEmail(email.value)

                //if textfields are empty, do nothing (likely user clicked by accident)
                if (email.value == "" || password.value == "" || retypePassword.value == "") {
                    //Do Nothing
                }

                //Check if we have a valid email
                else if ("@" !in email.value) { //User already exists
                    //if not, yell at user
                    showDialog3.value = true
                }

                //check if the user already exists. if we get a return from the database, they do
                else if (result != null) { //User already exists
                    //State that the email is taken
                    showDialog.value = true

                    //Check if passwords match
                }else if (password.value != retypePassword.value){
                    //If not, tell user they dont match
                    showDialog2.value = true
                }else {

                    //If all checks have passed, then we can add the user to the database, save the email, and let them sign in
                    viewModel.username.value = email.value
                    noteViewModel.username.value = email.value
                    appRepository.insertUser(username.value,"lastName-null", email.value, password.value)
                    nav.navigate("home"){
                        popUpTo("auth"){
                            inclusive = true
                        }
                    }


                    //val sender = GMailSender("timewise355@gmail.com", "")
                    //val recipients = arrayOf("southworthgraeme@gmail.com")
                    //val subject = "Timewise Sign-Up"
                    //val message = "Thank You For Signing Up with the App! All Critical Alert notifications will be sent from this email."
                    //sender.sendEmail(subject, message, recipients)
                }
            }
        },

            //Fancy configurations for button
            border = BorderStroke(3.dp, Turquoise3),
            colors = ButtonDefaults.buttonColors( containerColor = Color(0,150,152), contentColor = Color.White),
            modifier = Modifier
                .width(275.dp)
                .height(50.dp)

        ) {

            //Text field for button
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(20.dp))


        //Add some clickable text that allows the user to go back to login page
        Row {
            ClickableText(text = AnnotatedString("Go Back"),
                onClick = {
                    nav.navigate("login")
                })
        }

        //This is a debug function that adds a button to test database connections
        //Keep commented out unless for testing purposes
        //AddUserButton(appRepository)


    }
}

//Just for testing database operations, and creates a debug button on the sign up page for testing
//various functionalities, like inserting fields quickly into the database
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddUserButton(appRepository: AppRepository) {
    val context = LocalContext.current

    Button(onClick = {

        CoroutineScope(Dispatchers.IO).launch {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


//            appRepository.insertUser("Test12", "User", "gilbertijoma1@gmail.com", "password")
            appRepository.insertReminder("24", "gilbertijoma1@gmail.com", "My note 5", "Walk dog and feed him.",
                "High", currentDateTime.format(formatter), true, false, false )
            //val users = appRepository.getUserByEmail("test@example.com")
            //appRepository.insertNote("Sample Note", "2021-04-20", "This is a sample note text.", "gilbertijoma1@gmail.com")
            val users2 = appRepository.getRemindersByEmail("gilbertijoma1@gmail.com")
            launch(Dispatchers.Main) {
                //Toast.makeText(context, "User added: $users", Toast.LENGTH_SHORT).show()
                //Log.d("DatabaseTest", "Users: $users")
                Log.d("DatabaseTest", "Users: $users2")
            }
        }
    }) {
        Text("Add User")
    }
}