package com.eltonkola.kidztv.ui.settings.appmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.AppElement

class AppListAdapter constructor(context: Context, val editMode: Boolean, val onAction: (AppElement) -> Unit) :
    RecyclerView.Adapter<AppListAdapter.WordViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var data: List<AppElement> = emptyList()

    override fun getItemCount(): Int {
        return data.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val video_art: ImageView = itemView.findViewById(R.id.video_art)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val butDelete: ImageButton = itemView.findViewById(R.id.but_delete)

        fun bind(elem: AppElement) {
            title.text = elem.title

            if(editMode){
                butDelete.visibility = View.VISIBLE
                butDelete.setOnClickListener { onAction.invoke(elem) }
            }else{
                butDelete.visibility = View.GONE
                itemView.setOnClickListener { onAction.invoke(elem) }
            }


            video_art.setImageDrawable(elem.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.item_app_edit, parent, false)
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
