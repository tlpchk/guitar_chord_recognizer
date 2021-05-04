package com.example.chordrecognizer.view.history

import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chordrecognizer.R
import com.example.chordrecognizer.db.session.Session
import kotlinx.android.synthetic.main.history_item.view.*

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData


class HistoryAdapter :
    RecyclerView.Adapter<HistoryAdapter.SessionViewHolder>() {

    private var history: List<Session>? = null
    private var clickedSessionId : MutableLiveData<Long> = MutableLiveData()

    override fun getItemCount(): Int {
        return history?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SessionViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        if (history != null) {
            val session = history!![position]
            holder.bind(session)
        }
    }

    fun getClickedSessionId() = clickedSessionId

    fun setHistory(history: List<Session>) {
        this.history = history
        notifyDataSetChanged()
    }

    inner class SessionViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.history_item, parent, false)) {

        private var titleView: TextView = itemView.session_title_tv

        fun bind(session: Session){
            titleView.text = getDate(session.timestamp, "H:mm:ss dd/MM/yyyy")

            titleView.setOnClickListener {
                clickedSessionId.value = session.id
            }
        }

        private fun getDate(milliSeconds: Long, dateFormat: String): String {
            val formatter = SimpleDateFormat(dateFormat)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

    }
}