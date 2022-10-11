package info.tianguo.recyclerviewroomdemo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


/**
 * Dao = Data Access Object
 * Main class for defining your database interactions
 * Defines query methods for using the Homework class with Room.
 *
 */
@Dao
interface HomeworkDatabaseDao {

    /**
     * Add a row into the table with the name [homework_table]
     *
     * see [Homework] where the table name is defined
     */
    @Insert
    suspend fun insert(homework: Homework)


    /**
     * update a row with the new value, matched by primary key
     *
     * @param homework new value to write
     */
    @Update
    suspend fun update(homework: Homework)


    /**
     * delete a row from the table [homework_table]
     * that matches the supplied id, primary key.
     */
    @Query("DELETE FROM homework_table WHERE id= :key")
    suspend fun deleteHomework(key: Long)


    /**
     * Deletes all rows from the table [homework_table].
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM homework_table")
    suspend fun deleteAllHomework()


    /**
     * Selects and returns all rows in the table,
     *
     * Most recent homework first
     */
    @Query("SELECT * FROM homework_table ORDER BY id DESC")
    suspend fun getAllHomework(): List<Homework>


    /**
     * Selects and returns the latest homework.
     *
     * if another thread insert to the database before we select
     * we might get a different homework instance as what we inserted
     * this is fine for the current operation though will cause problems when the other thread runs selects
     * i.e., the same homework will return
     *
     * this problem can be potentially solved by using a different Homework entity design
     * e.g., instead of asking sqlite to manage primary key, we manage the id ourselves
     */
    @Query("SELECT * FROM homework_table ORDER BY id DESC LIMIT 1")
    suspend fun getMostRecentHomework(): Homework?



    /**
     * Selects and returns the row that matches the supplied id, primary key.
     */
    @Query("SELECT * from homework_table WHERE id = :key")
    suspend fun get(key: Long): Homework?

}