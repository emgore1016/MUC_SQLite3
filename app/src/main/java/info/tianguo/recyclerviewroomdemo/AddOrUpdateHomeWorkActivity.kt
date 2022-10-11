package info.tianguo.recyclerviewroomdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

const val HOMEWORK_ID = "id"
const val HOMEWORK_TITLE = "name"
const val HOMEWORK_DESCRIPTION = "description"
const val HOMEWORK_POSITION = "position"

class AddOrUpdateHomeWorkActivity : AppCompatActivity() {
    private lateinit var addHomeworkTitle: TextInputEditText
    private lateinit var addHomeworkDescription: TextInputEditText

    // variables used when updating an existing homework
    // for creating a new homework, set to default value of -1/L
    private var homeworkPosition: Int = -1
    private var homeworkID: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_homework_layout)

        addHomeworkTitle = findViewById(R.id.add_homework_title)
        addHomeworkDescription = findViewById(R.id.add_homework_description)

        //
        if (intent.hasExtra(HOMEWORK_ID)){
            _updateUI()
            homeworkPosition = intent.getIntExtra(HOMEWORK_POSITION, -1)
            homeworkID = intent.getLongExtra(HOMEWORK_ID, -1L)
        }


        findViewById<Button>(R.id.done_button).setOnClickListener {
            addOrUpdateHomework()
        }

    }

    /* The onClick action for the done button. Closes the activity and returns the new homework name
    and description as part of the intent. If the name or description are missing, the result is set
    to cancelled. */
    private fun addOrUpdateHomework() {
        val resultIntent = Intent()

        if (addHomeworkTitle.text.isNullOrEmpty() || addHomeworkDescription.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = addHomeworkTitle.text.toString()
            val description = addHomeworkDescription.text.toString()
            resultIntent.putExtra(HOMEWORK_TITLE, name)
            resultIntent.putExtra(HOMEWORK_DESCRIPTION, description)

            // we want to set a flag
            if (intent.hasExtra(HOMEWORK_TITLE)){
                resultIntent.putExtra(HOMEWORK_POSITION, homeworkPosition)
                resultIntent.putExtra(HOMEWORK_ID, homeworkID)
            }

            setResult(Activity.RESULT_OK, resultIntent)
        }

        // return to the calling activity [MainActivity]
        finish()
    }


    /**
     * by default, we will show the add homework UI
     */
    private fun _updateUI(){
        findViewById<TextView>(R.id.textView).text = getString(R.string.update_homework)
        addHomeworkDescription.setText(intent.getStringExtra(HOMEWORK_DESCRIPTION))
        addHomeworkTitle.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra(HOMEWORK_TITLE))
    }
}