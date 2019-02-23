package com.eltonkola.kidztv.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.AppElement

class AppGridAdapter constructor(context: Context, val onAction: (AppElement) -> Unit) :

    RecyclerView.Adapter<AppGridAdapter.WordViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var data: List<AppElement> = emptyList()

    override fun getItemCount(): Int {
        return data.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(elem: AppElement) {
            title.text = elem.title
            appIcon.setImageDrawable(elem.icon)
            itemView.setOnClickListener { onAction.invoke(elem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.item_app, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(data[position])
    }

    internal fun setData(AppElements: List<AppElement>) {
        this.data = AppElements
        notifyDataSetChanged()
    }
}
