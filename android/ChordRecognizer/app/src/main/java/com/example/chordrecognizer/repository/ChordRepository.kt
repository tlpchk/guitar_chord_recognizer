package com.example.chordrecognizer.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chordrecognizer.db.ChordRoomDatabase
import com.example.chordrecognizer.db.sound.Chord
import com.example.chordrecognizer.db.sound.ChordDao
import com.example.chordrecognizer.db.session.Session
import com.example.chordrecognizer.db.session.SessionDao
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File



class ChordRepository(application: Application) {
    private val chordApi: ChordApi
    private val chordDao: ChordDao
    private val sessionDao: SessionDao
    private val prediction: MutableLiveData<String> = MutableLiveData()
    private var sessionId: Long = -1

    companion object{
        private const val MEDIA_TYPE: String = "video/mp4"
//        const val API_URL = "http://192.168.0.45:8000/"
        const val API_URL = "http://10.42.121.254:8080/"
        const val FORM_NAME = "sound"
    }

    init {
        chordApi = buildSoundApi()
        val db = ChordRoomDatabase.getInstance(application)
        chordDao = db.soundDao()
        sessionDao = db.sessionDao()
    }

    fun getPrediction() = prediction

    fun startSession() {
        ChordRoomDatabase.dbExecService.execute {
            sessionId = sessionDao.insert(Session())
        }
    }

    fun sendSound(file: File) {
        val form = getForm(file)
        val call = chordApi.uploadSound(form)
        ChordRoomDatabase.dbExecService.execute(Runnable {
            val soundId = chordDao.insert(Chord(null, file.path, sessionId))
            enqueueCall(call, soundId)
        })

    }

    fun getAllSessions(): LiveData<List<Session>> {
        return sessionDao.findAll()
    }

    fun getSoundBySessionId(sessionId: Long): LiveData<List<Chord>> {
        return chordDao.getChordsBySessionId(sessionId)
    }

    private fun buildSoundApi(): ChordApi {
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChordApi::class.java)
    }


    private fun getForm(file: File): MultipartBody.Part {
        val filePart = RequestBody.create(
            MediaType.parse(MEDIA_TYPE),
            file
        )
        return MultipartBody.Part.createFormData(FORM_NAME, file.name, filePart)
    }

    private fun enqueueCall(call: Call<ResponseBody>, id: Long) {
        call.enqueue(object : Callback<ResponseBody> {
            private var chord: String = "-"

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.body() != null) {
                    chord = response.body()!!.string()
                }
                updatePrediction(id, chord)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                updatePrediction(id, chord)
            }
        })
    }

    private fun updatePrediction(id: Long, newPredictionValue: String) {
        prediction.value = newPredictionValue
        ChordRoomDatabase.dbExecService.execute(Runnable {
            chordDao.updateClass(id, newPredictionValue)
        })
    }
}
