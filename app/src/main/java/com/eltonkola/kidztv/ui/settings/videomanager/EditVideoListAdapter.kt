package com.eltonkola.kidztv.ui.settings.videomanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.VideoElement

class EditVideoListAdapter constructor(context: Context, val onDelete: (VideoElement) -> Unit) :
    RecyclerView.Adapter<EditVideoListAdapter.WordViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var data: List<VideoElement> = emptyList()

    override fun getItemCount(): Int {
        return data.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val video_art: ImageView = itemView.findViewById(R.id.video_art)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val butDelete: ImageButton = itemView.findViewById(R.id.but_delete)

        fun bind(videoElement: VideoElement) {
            title.text = videoElement.file.name
            butDelete.setOnClickListener { onDelete.invoke(videoElement) }
            video_art.setImageBitmap(videoElement.thumbnail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.item_video_edit, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(data[position])
    }

    internal fun setVideoElements(VideoElements: List<VideoElement>) {
        this.data = VideoElements
        notifyDataSetChanged()
    }
}
