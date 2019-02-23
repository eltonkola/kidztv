package com.eltonkola.kidztv.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.model.settings.BaseMenuItem
import com.eltonkola.kidztv.model.settings.SettingsMenuItem
import kotlinx.android.synthetic.main.item_settings_list.view.*


class MenuAdapter(
    private val values: List<BaseMenuItem>,
    private val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings_list, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings_list_section, parent, false)
            return SeparatorViewHolder(view)

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (values[position] is SettingsMenuItem) {
            0
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]

        if (holder is SeparatorViewHolder) {
            holder.title.text = item.title
        } else if (holder is ViewHolder) {

            holder.icon.setImageDrawable(
                (item as SettingsMenuItem).icon
            )
            holder.title.text = item.title

            if(item.external){
                holder.iconArrow.setImageResource(R.drawable.ic_call_made_24dp)
            }else{
                holder.iconArrow.setImageResource(R.drawable.ic_chevron_right_24dp)
            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.icon
        val iconArrow: ImageView = view.icon_arrow
        val title: TextView = view.title
    }

    inner class SeparatorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.title
    }
}