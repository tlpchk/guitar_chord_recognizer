package com.example.chordrecognizer.viewmodel

import android.app.Application
import android.os.CountDownTimer
import android.os.Environment
import android.os.Environment.DIRECTORY_MUSIC
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chordrecognizer.db.sound.Chord
import com.example.chordrecognizer.recorder.Recorder
import com.example.chordrecognizer.repository.ChordRepository
import java.io.File

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val recorder: Recorder
    private val chordRepository: ChordRepository

    private val displayText: MutableLiveData<String>
    private val progress: MutableLiveData<Int>
    private val recording: MutableLiveData<Boolean>

    private val handler: Handler
    private val progressTimer: CountDownTimer

    init {
        recorder = Recorder(getRootPath())
        chordRepository = ChordRepository(application)

        displayText = chordRepository.getPrediction()
        progress = MutableLiveData(100)
        recording = MutableLiveData(false)

        handler = Handler()
        progressTimer = buildProgressTimer()
    }

    companion object {
        const val TIMER_TIME = 2000L
        const val READY_DISPLAY_TEXT = "READY"
        const val START_DISPLAY_TEXT = "START"
    }

    fun getDisplayText(): LiveData<String> = displayText

    fun getProgressValue(): LiveData<Int> = progress

    fun getRecordingValue(): LiveData<Boolean> = recording

    fun onControllerClick() {
        val isRecording = recorder.isRecording()
        if (!isRecording) {
            startRecording()
        } else {
           stopRecording()
        }
    }

    private fun startRecording() {
        displayText.value = READY_DISPLAY_TEXT

        handler.removeCallbacksAndMessages(null)
        progressTimer.cancel()

        handler.postDelayed({
            chordRepository.startSession()
            recorder.startRecording {
                chordRepository.sendSound(it)
            }
        }, TIMER_TIME - Recorder.DURATION)

        progressTimer.start()
    }

    private fun stopRecording() {
        recorder.stopRecording()
        progressTimer.cancel()

        recording.value = false
        progress.value = 100
        displayText.value = START_DISPLAY_TEXT
    }

    fun getAllSessions(): LiveData<List<com.example.chordrecognizer.db.session.Session>> {
        return chordRepository.getAllSessions()
    }

    fun getSoundBySessionId(sessionId: Long): LiveData<List<Chord>> {
        return chordRepository.getSoundBySessionId(sessionId)
    }

    private fun getRootPath(): String {
        val rootPath =
            "${Environment.getExternalStorageDirectory().absolutePath}/$DIRECTORY_MUSIC/ChordRecognizer"
        val rootDir = File(rootPath)
        if (!rootDir.exists()) {
            rootDir.mkdir()
        }
        return rootPath
    }

    private fun buildProgressTimer(): CountDownTimer {
        return object : CountDownTimer(TIMER_TIME, 100) {
            override fun onTick(millisUntilFinished: Long) {
                progress.value = 100 - (millisUntilFinished / 20).toInt()
            }

            override fun onFinish() {
                progress.value = 100
                recording.value = true
            }
        }
    }
}

