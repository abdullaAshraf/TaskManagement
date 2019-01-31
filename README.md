# TaskManagement
Android app with firebase that enables the user to manage personal and organizations tasks.
# Project Status
This project is currently in development. Users can add and manage tasks, split the tasks to subtasks and assign subtasks to subordinate in any of their organizations.
Also users can look up organizations and apply for them , manage incoming invitations , add and manage organizations, invite members , manage their privileges and edit the organization hierarchy
## Screen shots
# Reflection
This is a personal project to practise java for android and firebase.

I wanted an app to manage all my personal tasks in addition to multiple organizations tasks like companies, student activities or even reading groups and also make it easy to manage large scale projects by allowing committee head in the organization to split the task to small objectives and assign them to other members and so on until
every member ends with a simple task to do.

# Implementation
The idea of the app is to allow users to add tasks and store it using offlie `SQLite`database, to add a task the user has to fill some basic fields, tasks are viewed in an `Expandable List View` to allow users to add subtasks to any task he owns and use dialog with spinners to assign those tasks to one of his organization members, users can fully manage organizations depending on their privileges, when the user is connected to an internet network the app uses an online `Firebase` database to synchronize user tasks with the updated data on the server.


