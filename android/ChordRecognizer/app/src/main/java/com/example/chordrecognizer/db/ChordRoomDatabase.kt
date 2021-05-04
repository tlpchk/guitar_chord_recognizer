package com.example.chordrecognizer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chordrecognizer.db.session.Session
import com.example.chordrecognizer.db.session.SessionDao
import com.example.chordrecognizer.db.sound.Chord
import com.example.chordrecognizer.db.sound.ChordDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [Chord::class, Session::class], version = 1)
abstract class ChordRoomDatabase : RoomDatabase() {
    abstract fun soundDao(): ChordDao
    abstract fun sessionDao(): SessionDao

    companion object {
        private var instance: ChordRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 10
        private const val DB_NAME = "chord_database"

        val dbExecService: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getInstance(context: Context): ChordRoomDatabase {
            if (instance == null) {
                synchronized(ChordRoomDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChordRoomDatabase::class.java,
                        DB_NAME
                    ).build()
                }
            }
            return instance!!
        }
    }
}