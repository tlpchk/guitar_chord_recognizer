package com.example.chordrecognizer.view.session

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chordrecognizer.R
import com.example.chordrecognizer.db.sound.Chord
import kotlinx.android.synthetic.main.session_item.view.*

class SessionAdapter:
    RecyclerView.Adapter<SessionAdapter.ChordViewHolder>() {

    private var session: List<Chord>? = null

    override fun getItemCount(): Int {
        return session?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChordViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ChordViewHolder, position: Int) {
        if (session != null) {
            val session = session!![position]
            holder.bind(session)
        }
    }

    fun setSession(session : List<Chord>) {
        this.session = session
        notifyDataSetChanged()
    }

    inner class ChordViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.session_item, parent, false)) {

        private var predictedClassView: TextView = itemView.predicted_class_tv
        private var secondView: TextView = itemView.second_tv

        fun bind(chord: Chord) {
            predictedClassView.text = chord.predictedClass
            secondView.text = getSecond()
        }

        private fun getSecond(): String {
            return "%02d:%02d".format(adapterPosition / 60,adapterPosition % 60)
        }

    }
}