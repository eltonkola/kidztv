package com.eltonkola.kidztv.ui.openVideoPlugin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.squareup.picasso.Picasso

class OpenVideoListAdapter constructor(context: Context, val onPlay: (OpenVideoElement) -> Unit) :
    RecyclerView.Adapter<OpenVideoListAdapter.WordViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var data: List<OpenVideoElement> = emptyList()

    override fun getItemCount(): Int {
        return data.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val videoArt: ImageView = itemView.findViewById(R.id.video_art)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val butDownload: ImageView = itemView.findViewById(R.id.but_download)

        fun bind(elem: OpenVideoElement) {

            title.text = elem.title
            description.text = elem.description
            butDownload.setOnClickListener { onPlay.invoke(elem) }

            if (!elem.alreadyDownloaded) {
                butDownload.visibility = View.VISIBLE
            } else {
                butDownload.visibility = View.GONE
            }
            Picasso.get().load(elem.thumb).into(videoArt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.item_open_video, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(data[position])
    }

    internal fun setOpenVideoElements(OpenVideoElements: List<OpenVideoElement>) {
        this.data = OpenVideoElements
        notifyDataSetChanged()
    }
}
