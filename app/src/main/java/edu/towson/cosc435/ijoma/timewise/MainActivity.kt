package edu.towson.cosc435.ijoma.timewise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import edu.towson.cosc435.ijoma.timewise.screens.AppDashboard
import edu.towson.cosc435.ijoma.timewise.screens.LoginScreen
import edu.towson.cosc435.ijoma.timewise.screens.SignUpScreen
import edu.towson.cosc435.ijoma.timewise.ui.theme.TimewiseTheme
import edu.towson.cosc435.ijoma.timewise.viewmodels.NoteViewModel
import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
import kotlinx.coroutines.*




class MainActivity : ComponentActivity() {

    //Create the View Models for the Notes and Reminders that will be used throughout the app
    private val viewModel by viewModels<ReminderViewModel>()
    private val noteViewModel by viewModels<NoteViewModel>()

    //Create a Cocourtine Scope for calling the database
    private val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimewiseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    //Call the Screen Handler for the App, takes the two View Models we want
                    //To pass for the entire application
                    InitialScreen(viewModel, noteViewModel)
                }
            }
        }
    }


    @Composable
    fun InitialScreen(viewModel: ReminderViewModel, noteViewModel: NoteViewModel) {
        /*
        This function handles setting up the Nav Controller for our application, where we set up the different
        Screens for our application to use. This Nav Controller contains nested navigation, sub categorizing our
        authorization screens with our app dashboard. This allows free navigation between auth pages, but pops them when
        the user logs in
         */

        val nav = rememberNavController()

        //Initial Screen when the user logs in, we send them to the login page
        NavHost(navController = nav, startDestination = "auth") {
            navigation(
                startDestination = "login",
                route = "auth"
            ) {
                composable("login") {
                    //Composable function that handles the login screen, pass the Reminder and Note View Models
                    LoginScreen(nav, viewModel, noteViewModel)
                }
                composable("register") {
                    //Composable function that handles setting up the sign up screen, pass the Reminder and Note view Models
                    SignUpScreen(nav, viewModel, noteViewModel)
                }
            }

            //Second Navigation. When we enter these screens, we want to pop all previous authorization screens
            //We initially land people on the App Dashboard
            navigation(
                startDestination = "task_overview",
                route = "home"
            ) {
                composable("task_overview") {
                    AppDashboard(nav, viewModel, noteViewModel, applicationContext)

                }
                //NOT USED ANYMORE. Originailly for displaying notes on a seperate screen, decided to keep it all
                //On popups (looked prettier
                //composable("notes") {
                //    NotesScreen(nav)
                //}
            }
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
        val navGraphRoute = destination.parent?.route ?: return viewModel()
        val parentEntry = remember(this) {
            navController.getBackStackEntry(navGraphRoute)
        }
        return viewModel(parentEntry)
    }


//Test Preview Screen
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        TimewiseTheme {
//        NotesScreen()
        }
    }

