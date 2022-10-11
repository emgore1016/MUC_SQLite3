package info.tianguo.recyclerviewroomdemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// first annotate this to be an entity (a sqlite table)
@Entity(tableName = "homework_table")
class Homework(
    // note that sqlite will assign the ID to homework insert to the database
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var title: String = "",

    // you can use the annotation @ColumnInfo to have a different sqlite table column name
    @ColumnInfo(name = "desc")
    var description: String = ""
    )