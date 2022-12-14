# RecyclerViewRoomDemo

This is a demo application that builds on top of our previous RecyclerView Demo.

This app provides the following features: 

- the key feature is data persistence! If you compare this version to the RecyclerView Demo (CalendarDialog), you shall see that all homework will be kept safe even if you relaunch or restart the app.
  - this is achieved by using an on-device database

- the floating action button will take the user to a different screen
  - if both field are filled, the new homework will be inserted to the database and the UI will be updated 
  - the list currently is displayed in a descending order of the homework's ID 
    - this is achieved in two parts: using an automatic increment id column, returning existing rows ordered by their ids, and inserting the newly created homework to the top of the LiveData<List>
    
- the cardview supports long press that will take the user to a different screen 
  - the screen will be pre-filled with the relevant homework data, facilitated by intent extra 
  - if user decides not to update anything, the application states do not change 

- the cardview also supports swiping left or right to delete a homework 

## Debugging notes 

If you are not familiar with SQL statements, it is always a good idea to test the statements on the local database copy first to make sure the statement does exactly what you expect
- you can download a copy of database from the AVD to your development machine
- you will need use the built-in Terminal of the Android Studio to execute the SQL statements 
- you can view the database changes either via Terminal or using a SQLite Viewer as introduced in class 


## Design notes 

- I tried to minimize the amount of refactoring when integrating with Room. Most code changes can be found in the following files (or you can use git diff yourself to filter the changes).
  - database/ module 
  - AddOrUpdateHomeWorkActivity.kt 
  - MainActivity.kt

- For simplicity, we will just update the database everytime the user creates a new homework, update an existing homework, and delete a homework. 
  - in real-world apps, you probably want to work with in-memory data as much as possible and only try to save the data to the database when the user navigates from the activity. 
#   M U C _ S Q L i t e 3  
 