package com.example.chordrecognizer.view.controller

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chordrecognizer.R
import com.example.chordrecognizer.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_controller.*
import kotlinx.android.synthetic.main.fragment_controller.view.*


class ControllerFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private var recording: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(false)
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_controller, container, false)

        view.setOnClickListener {
            viewModel.onControllerClick()
        }

        viewModel.apply {
            getRecordingValue().observe(viewLifecycleOwner, Observer<Boolean> {
                recording = it
                setProgressStyle(it)
            })

            getDisplayText().observe(viewLifecycleOwner, Observer<String> {
                view.display_tv.text = it
            })

            getProgressValue().observe(viewLifecycleOwner, Observer<Int> {
                view.progressBar.setProgress(it, true)
            })
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.app_name)
        view?.display_tv?.text = resources.getString(R.string.start)
    }

    override fun onPause() {
        super.onPause()
        if (recording) {
            view?.callOnClick()
        }
    }

    private fun setProgressStyle(recording: Boolean) {
        if (activity != null) {
            progressBar.progressTintList = if (recording) {
                ColorStateList.valueOf(activity!!.getColor(R.color.colorRecord))
            } else {
                ColorStateList.valueOf(activity!!.getColor(R.color.colorAccent))
            }
        }
    }
}