package info.tianguo.recyclerviewroomdemo

import android.app.Activity
import android.app.DatePickerDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import info.tianguo.recyclerviewroomdemo.database.Homework
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeworkAdapter"

class HomeworkAdapter(val activity: Activity, private val homework: List<Homework>) :
    RecyclerView.Adapter<HomeworkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_homework, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val choice = homework[position]

        // so that we can use the position to update the livedata
        holder.bind(position, choice)
    }

    override fun getItemCount(): Int {
        return homework.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView
        private val descText: TextView
        private val launchButton: Button

        private val dateText: TextView
        private val cardView: CardView

        fun bind(position: Int, homework: Homework) {
            titleText.text = homework.title
            descText.text = homework.description
            launchButton.setOnClickListener {
                Log.d(TAG, "button pressed")
                // launch the Calendar Dialog
                val newCalendar: Calendar = Calendar.getInstance()
                val startTime = DatePickerDialog(it.context,
                    { view, year, monthOfYear, dayOfMonth ->
                        val newDate: Calendar = Calendar.getInstance()
                        newDate.set(year, monthOfYear, dayOfMonth)
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        Log.d(TAG, "date: ${simpleDateFormat.format(newDate.time)}")
                        dateText.text = simpleDateFormat.format(newDate.time)
                    },
                    newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH)
                )
                startTime.show()
            }

            cardView.setOnLongClickListener {
                Toast.makeText(activity,"long clicked", Toast.LENGTH_SHORT).show()
                // start the pre-filled addHomeworkActivity
                val mainActivity: MainActivity = activity as MainActivity
                mainActivity.updateHomework(position, homework)

                true
            }

        }

        init {
            titleText = itemView.findViewById(R.id.item_title)
            descText = itemView.findViewById(R.id.item_description)
            launchButton = itemView.findViewById(R.id.item_launch_button)
            cardView = itemView.findViewById(R.id.cardview)
            dateText = itemView.findViewById(R.id.item_date)
        }
    }
}