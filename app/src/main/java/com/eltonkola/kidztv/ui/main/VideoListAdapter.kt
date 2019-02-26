package com.eltonkola.kidztv.ui.main

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.VideoElement

class VideoListAdapter constructor(context: Context, val onPlay: (VideoElement) -> Unit) :
    RecyclerView.Adapter<VideoListAdapter.WordViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var data: List<VideoElement> = emptyList()

    override fun getItemCount(): Int {
        return data.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val video_art: ImageView = itemView.findViewById(R.id.video_art)
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(videoElement: VideoElement) {
            itemView.layoutParams.width = (getWidthScreen(itemView.context) * 0.2f).toInt()

            title.text = videoElement.file.name
            itemView.setOnClickListener { onPlay.invoke(videoElement) }
            video_art.setImageBitmap(videoElement.thumbnail)
        }
    }

    var widthScreen = -1

    fun getWidthScreen(context: Context): Int {
        if (widthScreen == -1) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            widthScreen = size.x
        }
        return widthScreen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.item_video, parent, false)
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
