package com.example.chordrecognizer.db.sound

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.chordrecognizer.db.session.Session

@Entity(foreignKeys = [ForeignKey(
    entity = Session::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("sessionId"),
    onDelete = ForeignKey.CASCADE
)])
data class Chord(
    var predictedClass: String?,
    var filePath: String,
    var sessionId: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)