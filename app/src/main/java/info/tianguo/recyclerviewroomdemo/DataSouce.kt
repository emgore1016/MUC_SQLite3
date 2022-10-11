package info.tianguo.recyclerviewroomdemo

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import info.tianguo.recyclerviewroomdemo.database.Homework
import info.tianguo.recyclerviewroomdemo.database.HomeworkDatabase
import kotlinx.coroutines.*

private const val TAG = "DataSource"

class DataSource private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    val database = HomeworkDatabase.getInstance(BaseApplication.getAppContext()).homeworkDatabaseDao


    // private val initialChoiceList = info.tianguo.recyclerviewroomdemo.getHomeworkList()
    // private val choicesLiveData = MutableLiveData(initialChoiceList)

    private lateinit var  choicesLiveData: MutableLiveData<List<Homework>>

    init {
        coroutineScope.launch(Dispatchers.IO) {
            choicesLiveData = MutableLiveData(database.getAllHomework())
        }
    }


    suspend fun insertHomework(title: String?, description: String?){
        Log.d(TAG, "insertHomework")
        if (title == null || description == null) {
            return
        }

        // if we have a valid homework
        val newHomework = Homework()
        newHomework.title = title
        newHomework.description = description

        // to get sqlite to assign a unique id to our homework
        database.insert(newHomework)

        val mostRecentHomework = getMostRecentHomeworkFromDB()
        if (mostRecentHomework != null){
            Log.d(TAG, "trying to addHomework")
            addHomework(mostRecentHomework)
        }
    }

    suspend fun updateHomework(position: Int, id: Long, title: String?, description: String?) {
        Log.d(TAG, "updateHomework")

        // for some reason, users delete either fields
        // but since the table's column title and description is not null, i.e., String instead of String?
        var updatedTitle = ""
        var updateDescription = ""

        if (title != null) {
            updatedTitle = title
        }

        if (description != null) {
            updateDescription = description
        }

        // create an updated homework
        val newHomework = Homework(id, updatedTitle, updateDescription)
        Log.d(TAG, "The homework's id is: ${newHomework.id}")
        // update the homework
        database.update(newHomework)
        _updateHomework(position, newHomework)

    }


    suspend fun deleteHomework(homework: Homework){
        database.deleteHomework(homework.id)
        removeHomework(homework)
    }



    private suspend fun getHomeworkFromDB(id: Long): Homework?{
        return database.get(id)
    }

    private suspend fun getMostRecentHomeworkFromDB(): Homework?{
        Log.d(TAG, "getMostRecentHomeworkFromDB")
        val homework = database.getMostRecentHomework()
        Log.d(TAG, "homework title: ${homework?.title}; homework desc: ${homework?.description}")
        if (homework?.title != "" && homework?.description != ""){
            Log.d(TAG, "return the most recent Homework")
            return homework
        }

        // don't seem to have a
        return null
    }

    /**
     * Adds homework to liveData and posts value.
     */
    private fun addHomework(homework: Homework){

        val currentList = choicesLiveData.value
        if (currentList == null){
            Log.d(TAG, "our first homework")
            choicesLiveData.postValue(listOf(homework))
        } else {
            Log.d(TAG, "update the current homework list")
            val updatedList = currentList.toMutableList()
            updatedList.add(0, homework)
            choicesLiveData.postValue(updatedList)
        }
    }


    private fun _updateHomework(position: Int, homework: Homework){

        Log.d(TAG, "_updateHomework: to notify the LiveData observer!")
        val currentList = choicesLiveData.value
        if (currentList == null){
            Log.d(TAG, "the list should not be null. Something went wrong!")
            choicesLiveData.postValue(listOf(homework))
        } else {
            Log.d(TAG, "update the homework at position $position")
            val updatedList = currentList.toMutableList()
            updatedList[position] = homework
            choicesLiveData.postValue(updatedList)
        }
    }

    /**
     * Removes homework from liveData and posts value.
     */
    fun removeHomework(homework: Homework) {
        val currentList = choicesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(homework)
            choicesLiveData.postValue(updatedList)
        }

    }

    fun getHomeworkList(): LiveData<List<Homework>>{
        return choicesLiveData
    }

    companion object {
        @Volatile
        private var INSTANCE: DataSource? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DataSource(context)
            }
        }

        fun getDataSource(): DataSource {
            return INSTANCE
                ?: throw IllegalStateException("DataSource must be initialized")
        }

        // private var INSTANCE: DataSource? = null

        // fun getDataSource(): DataSource {
        //     return synchronized(DataSource::class) {
        //         val newInstance = INSTANCE ?: DataSource()
        //         INSTANCE = newInstance
        //         newInstance
        //     }
        // }
    }
}