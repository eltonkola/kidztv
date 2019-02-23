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
import java.text.SimpleDateFormat


class SettingsAppListAdapter constructor(context: Context, val editMode: Boolean, val onAction: (AppElement) -> Unit) :
    RecyclerView.Adapter<SettingsAppListAdapter.WordViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var data: List<AppElement> = emptyList()
    var sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS")
    override fun getItemCount(): Int {
        return data.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val subtitle: TextView = itemView.findViewById(R.id.subtitle)

        private val butDelete: ImageButton = itemView.findViewById(R.id.but_delete)
        private val butAdd: ImageButton = itemView.findViewById(R.id.but_add)


        fun bind(elem: AppElement) {
            title.text = elem.title


            if(editMode){
                subtitle.text = "Added: ${sdf.format(elem.dbModel.enabledDate)}"
                butDelete.visibility = View.VISIBLE
                butAdd.visibility = View.GONE
                butDelete.setOnClickListener { onAction.invoke(elem) }
            }else{
                subtitle.text = "Added: ${elem.dbModel.packageName}"
                butDelete.visibility = View.GONE
                butAdd.visibility = View.VISIBLE
                butAdd.setOnClickListener { onAction.invoke(elem) }
            }

            appIcon.setImageDrawable(elem.icon)
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
