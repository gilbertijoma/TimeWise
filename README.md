Welcome to the mobile application TimeWise, an app that helps you manage your tasks and 
reminders efficiently. TimeWise allows users to create reminders with priorities and add detailed
notes. Whether you need to remember a meeting, manage your shopping list, or outline a 
project, TimeWise is here to help you stay organized.

**Quick App Guide**

First simply sign up by clicking the sign up option on the login screen or login with your email 
and password if you already have an account.

Once you give your credentials the app dashboard will be revealed where your reminders and 
notes will be displayed.

To add a new reminder click on the  “+ New Reminder" button and a dialog box will display and 
ask you to enter the priority, date, time, title, and description of the reminder. These reminders 
will send out email notifications and app notifications when the local date and time is close to 
the inputted date and time.

To add a new note just simply click on the “+ New note” button and a dialog box will display and 
ask you enter the title and description of the note.

**App Structure**

MyApplication.java: Creates the database, notification methods execute on startup.

Database:
- DatabaseContract.kt: Contract class that specifies the layout of the schema
- dbHelper.kt: Helper class that extends SQLiteOpenHelper and manages database 
creation and version.
- AppRepository: Database methods
- Note, Reminder, User: classes for database tables

Notification System:
- EmailNotifications.kt: Sends emails to users for reminders using sendgrid API service
- Alarm.kt: Sends app notifications using alarm manager

UI:
- Popup files are for adding a reminder and note
- AppDashboard: Home screen for app; reminders and notes layout
- MainActivity: Sets the view models for the notes and reminder

3rd Party libraries and resources
RetroFit - Http requests for Sendgrid API
Sendgrid API  - Sending Email notifications for reminders
