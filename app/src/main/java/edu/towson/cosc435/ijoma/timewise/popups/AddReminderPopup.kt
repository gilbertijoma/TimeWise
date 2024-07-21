package edu.towson.cosc435.ijoma.timewise.popups

//import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
//import edu.towson.cosc435.ijoma.timewise.database.AppRepository
//import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import edu.towson.cosc435.ijoma.timewise.Alarm
import edu.towson.cosc435.ijoma.timewise.database.DatabaseHelper
import edu.towson.cosc435.ijoma.timewise.viewmodels.ReminderViewModel
import edu.towson.cosc435.ijoma.timewise.sampledata.AppRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun addReminderPopup(
    /*
    This monstrosity of a function handles creating a popup so the user can select from their choice of inputs to create
    A new reminder for their app (stored inside the database).

    This function takes the reminderViewModel class so we can add the reminder to the list of reminders displayed,
    and contect for the database.
     */

    viewModel: ReminderViewModel,
    context: Context,
    onDismiss: () -> Unit
){

    //This handles the states for all of the input fields that we want to remember and pass
    //to the reminder class
    val reminderTitle = remember { mutableStateOf("") }
    val reminderDesc = remember { mutableStateOf("") }
    val date = remember {mutableStateOf("")}
    val selectedPriority = remember {mutableStateOf("Low")}
    var dropdownState by remember {mutableStateOf(false)}
    var dateDropdownState by remember {mutableStateOf(false)}
    var timeDropdownState by remember {mutableStateOf(false)}
    val priority = listOf("Low", "Medium", "High")

    //Handles Dropdown values for current date and time
    val sdf = SimpleDateFormat("yyyy-MM-dd")


    val currentDateAndTime = sdf.format(Date())
    val selectedDate = remember {mutableStateOf(currentDateAndTime)}
    val selectedTime = remember {mutableStateOf("12:00 AM")}
    val dateList = getDates()

    val times = (0 until 24 * 60 / 15).map { quarterHour ->
        val hour = quarterHour * 15 / 60
        val minute = quarterHour * 15 % 60
        val hourStr = if (hour == 0) "12" else (hour % 12).toString()
        val minuteStr = if (minute < 10) "0$minute" else minute.toString()
        val amPm = if (hour < 12) "AM" else "PM"
        "$hourStr:$minuteStr $amPm"
    }


    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current




    AlertDialog(
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
                    Text(text = "Add Reminder",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Box(
                    modifier = Modifier
                        .width(260.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(text = "Priority:",
                            modifier = Modifier.weight(1f)
                        )

                        Column(){
                            // Display the selected item
                            Text(
                                text = selectedPriority.value,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        // Toggle the dropdown menu visibility
                                        dropdownState = !dropdownState
                                    })
                            )
                            // Dropdown menu
                            DropdownMenu(
                                expanded = dropdownState,
                                onDismissRequest = { dropdownState = false }
                            ) {
                                priority.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            // Update the selected item when an item is clicked
                                            selectedPriority.value = item
                                            dropdownState = false // Close the dropdown menu
                                        },
                                        text =
                                        {
                                            Text(text = item)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(260.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(text = "Date:",
                            modifier = Modifier.weight(1f)
                        )

                        Column(){
                            // Display the selected item
                            Text(
                                text = selectedDate.value,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        // Toggle the dropdown menu visibility
                                        dateDropdownState = !dateDropdownState
                                    })
                            )
                            // Dropdown menu
                            DropdownMenu(
                                expanded = dateDropdownState,
                                onDismissRequest = { dateDropdownState = false },
                                modifier = Modifier.height(150.dp)

                            ) {
                                dateList.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            // Update the selected item when an item is clicked
                                            selectedDate.value = item
                                            dateDropdownState = false // Close the dropdown menu
                                        },
                                        text =
                                        {
                                            Text(text = item)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(260.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(text = "Time:",
                            modifier = Modifier.weight(1f)
                        )

                        Column(){
                            // Display the selected item
                            Text(
                                text = selectedTime.value,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        // Toggle the dropdown menu visibility
                                        timeDropdownState = !timeDropdownState
                                    })
                            )
                            // Dropdown menu
                            DropdownMenu(
                                expanded = timeDropdownState,
                                onDismissRequest = { timeDropdownState = false },
                                modifier = Modifier.height(150.dp)

                            ) {
                                times.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            // Update the selected item when an item is clicked
                                            selectedTime.value = item
                                            timeDropdownState = false // Close the dropdown menu
                                        },
                                        text =
                                        {
                                            Text(text = item)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(value = reminderTitle.value,
                    onValueChange = {newReminderTitle -> reminderTitle.value = newReminderTitle },
                    label = { Text("Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)

                )

                Spacer(modifier = Modifier.height(5.dp))

                /*
                OutlinedTextField(value = date.value,
                    onValueChange = {newDate -> date.value = newDate },
                    label = {Text(currentDateAndTime)},
                    //label = { Text("Date") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                )
                */

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(value = reminderDesc.value,
                    onValueChange = {newReminderDesc -> reminderDesc.value = newReminderDesc },
                    label = { Text("Description") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                //
                Button(
                    onClick = {

                        lifecycleOwner.lifecycleScope.launch {

                            //val formatTime = selectedTime.value.substring(0)
                            val dbHelper = DatabaseHelper(context)
                            val appRepository = AppRepository(dbHelper)


                            val time = selectedTime.value.substring(0, 5)

                            appRepository.insertReminder(
                                (viewModel.mutableReminders.value.size+1).toString(), viewModel.username.value, reminderTitle.value, reminderDesc.value, selectedPriority.value, "2024-05-06 01:25:00", false, false, false)

                        }

                        //viewModel.onAdd(name = noteTitle.value, description = noteDesc.value, p riority = selectedPriority.value, date = selectedDate.value, time = selectedTime.value)
                        // Calls the scheduleAlarm function with the users inputed date,time, and reminder title.
                        scheduleAlarm(context, selectedDate.value, selectedTime.value, reminderTitle.value)


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

fun getDates(): MutableList<String>{
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = Calendar.getInstance()

    // Get the current date
    val currentDateStr = sdf.format(currentDate.time)
    println("Current Date: $currentDateStr")

    // Get dates 5 days behind and 5 days ahead
    val dateList = mutableListOf<String>()
    for (i in 0..5) {
        currentDate.add(Calendar.DAY_OF_MONTH, i)
        dateList.add(sdf.format(currentDate.time))
    }

    return dateList
}
// Schedule an alarm and display a notification
@RequiresApi(Build.VERSION_CODES.O)
fun scheduleAlarm(context: Context, selectedDate: String, selectedTime: String, noteTitle: String) {
    // Set the alarm using the selected time
    setAlarm(context, selectedTime, noteTitle, selectedDate)
    // Create a notification service instance and display the notification
    val notificationService = Alarm()
    notificationService.displayNotification(context, noteTitle, selectedDate, selectedTime)
}

// Set the alarm based on the selected time
fun setAlarm(context: Context, selectedTime: String, noteTitle: String, selectedDate: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Create an intent for the Alarm broadcast receiver
    val intent = Intent(context, Alarm::class.java)
    intent.putExtra("title", noteTitle)
    intent.putExtra("date", selectedDate)
    intent.putExtra("time", selectedTime)

    val pendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_IMMUTABLE)

    // Calculate the time for the alarm to trigger (currently hardcoded to 15000 milliseconds)
    val timeInMillis = System.currentTimeMillis() + 15000

    // Set the alarm using AlarmManager
    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
}


