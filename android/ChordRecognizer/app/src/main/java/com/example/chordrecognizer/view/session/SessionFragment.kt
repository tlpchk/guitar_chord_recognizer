package com.example.chordrecognizer.view.session


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chordrecognizer.R
import com.example.chordrecognizer.db.sound.Chord
import com.example.chordrecognizer.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_session.*


class SessionFragment(private val sessionId: Long) : Fragment() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
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
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLayoutManager = LinearLayoutManager(activity)
        val mAdapter = SessionAdapter()
        val divider = DividerItemDecoration(
            context,
            mLayoutManager.orientation
        )

        viewModel.getSoundBySessionId(sessionId).observe(viewLifecycleOwner, Observer<List<Chord>> {
            mAdapter.setSession(it)
        })

        session_rv.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
            addItemDecoration(divider)
        }
    }
}