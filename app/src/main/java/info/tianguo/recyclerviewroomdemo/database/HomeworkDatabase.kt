package info.tianguo.recyclerviewroomdemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * A database that stores homework information.
 * A global method to get access to the database.
 *
 * It is a common structure for on-device database
 *
 * You could also look up the repository pattern to see how to integrate the database code
 */
@Database(entities = [Homework::class], version = 1, exportSchema = false)
abstract class HomeworkDatabase: RoomDatabase() {

    /**
     * Connects the database to the DAO.
     */
    abstract val homeworkDatabaseDao: HomeworkDatabaseDao

    /**
     * use a companion object to provide access to the same database instance
     *
     * Use like `HomeworkDatabase.getInstance(context)`
     */
    companion object {
        /**
         *  volatile, meaning that writes to this field are immediately made visible to other threads.
         */
        @Volatile
        private var INSTANCE: HomeworkDatabase? = null

        /**
         *
         * If a database has already been retrieved, the previous database will be returned.
         * Otherwise, create a new database.
         *
         * Follows the Singleton pattern to avoid repeatedly initializing the database
         *
         * @param context The application context, for accessing the database file
         */
        fun getInstance(context: Context): HomeworkDatabase {
            // use `synchronized` to make this function threadsafe
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {

                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE

                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HomeworkDatabase::class.java,
                        // the file name for the database
                        // you can locate this file at /data/data/[yourpackagename]/databases
                        "homework_database"
                    )
                        // Wipes the data and rebuilds instead of migrating if no Migration object.
                        // if you want to keep the database data, you can do something like
                        // .addMigrations(MIGRATION_1_2) where MIGRATION_1_2 tells Room how to update the sqlite
                        // more at:
                        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }

                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }

}