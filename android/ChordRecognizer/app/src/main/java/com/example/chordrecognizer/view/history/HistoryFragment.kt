package com.example.chordrecognizer.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chordrecognizer.R
import com.example.chordrecognizer.view.session.SessionFragment
import com.example.chordrecognizer.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLayoutManager = LinearLayoutManager(activity)
        val mAdapter = HistoryAdapter()
        val divider = DividerItemDecoration(
            context,
            mLayoutManager.orientation
        )

        viewModel.getAllSessions().observe(viewLifecycleOwner, Observer {
            mAdapter.setHistory(it)
        })

        mAdapter.getClickedSessionId().observe(viewLifecycleOwner, Observer {
            replaceWithSessionFragment(it)
        })

        history_rv.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
            addItemDecoration(divider)

        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.history_title)
    }

    private fun replaceWithSessionFragment(sessionId: Long) {
        val frag = SessionFragment(sessionId)
        activity?.apply {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frag_container, frag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit()
        }
    }

}