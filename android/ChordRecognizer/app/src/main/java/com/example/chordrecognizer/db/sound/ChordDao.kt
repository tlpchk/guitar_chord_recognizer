package com.example.chordrecognizer.db.sound

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChordDao {
    @Insert
    fun insert(chord: Chord) : Long

    @Query("UPDATE chord SET predictedClass = :predictedClass WHERE id = :id")
    fun updateClass(id: Long, predictedClass: String): Int

    @Query("SELECT * FROM chord WHERE sessionId=:sessionId")
    fun getChordsBySessionId(sessionId: Long): LiveData<List<Chord>>
}