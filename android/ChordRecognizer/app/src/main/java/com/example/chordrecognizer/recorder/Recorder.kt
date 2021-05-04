package com.example.chordrecognizer.recorder

import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class Recorder(private val rootPath: String) {
    private lateinit var fileToWrite: File
    private lateinit var fileToSend: File
    private var nativeRecorder: MediaRecorder? = null
    private var recording = false

    companion object {
        const val DURATION = 1000L
        const val BIT_RATE = 256000
        const val JUNK_FILE_NAME = "junk"
        const val FILE_FORMAT = "mp4"
    }

    fun isRecording() = recording

    fun startRecording(onRecord: (File) -> Unit) {
        recording = true
        fileToWrite = getNewFile(JUNK_FILE_NAME)
        nativeRecorder = buildMediaRecorder()
        nativeRecorder?.apply {
            setOnInfoListener(buildInfoListener(onRecord))
            start()
        }
    }

    fun stopRecording() {
        recording = false
        nativeRecorder?.apply {
            try {
                stop()
            } catch (e: RuntimeException) {
                fileToWrite.delete()
            }
            release()
        }
        nativeRecorder = null
    }


    private fun buildMediaRecorder(): MediaRecorder {
        return MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(BIT_RATE)
            setAudioSamplingRate(44100)
            setMaxFileSize((BIT_RATE / 8 / 0.94).toLong()) // one second
            setOutputFile(fileToWrite)

            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun buildInfoListener(onRecord: (File) -> Unit): MediaRecorder.OnInfoListener {
        return MediaRecorder.OnInfoListener { mr, what, _ ->
            when (what) {
                MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING -> {
                    val fileNameToWrite = System.currentTimeMillis().toString()
                    fileToSend = fileToWrite
                    fileToWrite = getNewFile(fileNameToWrite)
                    mr.setNextOutputFile(fileToWrite)
                }

                MediaRecorder.MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED -> {
                    if (fileToSend.name != "$JUNK_FILE_NAME.$FILE_FORMAT") {
                        onRecord(fileToSend)
                    }
                }
            }
        }
    }

    private fun getNewFile(fileName: String): File {
        return File("${rootPath}/$fileName.$FILE_FORMAT")
    }

}