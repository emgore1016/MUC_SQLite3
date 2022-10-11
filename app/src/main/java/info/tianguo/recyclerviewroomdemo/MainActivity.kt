package info.tianguo.recyclerviewroomdemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.tianguo.recyclerviewroomdemo.database.Homework
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var mRecycler: RecyclerView
    private lateinit var adapter: HomeworkAdapter
    private lateinit var dataSource: DataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecycler = findViewById(R.id.homework_recycler)
        mRecycler.layoutManager = LinearLayoutManager(this)

        dataSource = DataSource.getDataSource()
        val homeworkLiveData = dataSource.getHomeworkList()

        homeworkLiveData.observe(this) {
            it?.let {
                Log.d(TAG, "Observing the homeworkLiveData")
                adapter = HomeworkAdapter(this, it)
                mRecycler.adapter = adapter
            }
        }

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            fabOnClick()
        }

        setRecyclerViewItemTouchListener()
    }


    private fun setRecyclerViewItemTouchListener() {

        //1
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                //2
                return false
            }

            /**
             * A careful user will notify that swiping to delete a homework will cause the calendar data to
             * disappear from the item view
             * can you figure out why? better, can you fix the issue by making small modifications?
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //3
                val position = viewHolder.bindingAdapterPosition
                val currentHomework: Homework? =  dataSource.getHomeworkList().value?.get(position)
                if (currentHomework != null){
                    // delete is quite easy
                    // delete from db and also update the livedata
                    // use [GlobalScope] to make sure the delete from db completes even if the activity is destroyed
                    GlobalScope.launch(Dispatchers.IO) {
                        dataSource.deleteHomework(currentHomework)
                    }
                }
            }
        }

        //4
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(mRecycler)
    }


    /**
     * this shows launching the activity using the newer API
     * registerForActivityResult
     */
    fun fabOnClick() {
        val intent = Intent(this, AddOrUpdateHomeWorkActivity::class.java)
        resultLauncher.launch(intent)
    }

    /**
     * the itemview should have all the information about the homework
     * in this case, might be cheaper to pass the text fields than passing the id (for issuing a database request)
     *
     * this method will be called by the itemview event listener method
     */
    fun updateHomework(position: Int, homework: Homework){
        // reuse the same activity UI
        val intent = Intent(this, AddOrUpdateHomeWorkActivity::class.java)

        with (intent){
            Log.d(TAG, "updateHomework: configure the intent")
            // used to update the data in the livedata list
            putExtra(HOMEWORK_POSITION, position)

            // used to auto-fill the homework information in the addHomeworkActivity
            putExtra(HOMEWORK_ID, homework.id)
            putExtra(HOMEWORK_TITLE, homework.title)
            putExtra(HOMEWORK_DESCRIPTION, homework.description)
        }

        resultLauncher.launch(intent)
    }


    // pay attention to the syntax
    // the anonymous function is the ActivityResultCallback
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            // do you know why?
            result.data?.let { data ->
                val homeworkTitle = data.getStringExtra(HOMEWORK_TITLE)
                val homeworkDescription = data.getStringExtra(HOMEWORK_DESCRIPTION)

                Log.d(TAG, "trying to update the new homework")

                if (data.hasExtra(HOMEWORK_POSITION)){
                    Log.d(TAG, "resultLauncher: updating existing homework")
                    GlobalScope.launch(Dispatchers.IO){
                        Log.d(TAG, "The homework ID passed by intent is: ${data.getLongExtra(
                            HOMEWORK_ID, 0L)}")
                        dataSource.updateHomework(
                            data.getIntExtra(HOMEWORK_POSITION, 0),
                            data.getLongExtra(HOMEWORK_ID, 0L),
                            homeworkTitle,
                            homeworkDescription)
                    }
                } else{
                    Log.d(TAG, "resultLauncher: creating a new homework")

                    // GlobalScope is alive as long as you app is alive,
                // if you doing some counting for instance in this scope and rotate your device it will continue the task/process.
                GlobalScope.launch(Dispatchers.IO) {
                    dataSource.insertHomework(homeworkTitle,homeworkDescription)
                }
            }
            }
        }
    }


}