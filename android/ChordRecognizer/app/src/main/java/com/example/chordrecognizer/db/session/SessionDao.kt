package com.example.chordrecognizer.db.session

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SessionDao {
    @Insert
    fun insert(session: Session) : Long

    @Query("SELECT * FROM session")
    fun findAll(): LiveData<List<Session>>
}