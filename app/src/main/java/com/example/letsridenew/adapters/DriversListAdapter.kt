package com.example.letsridenew.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.letsridenew.R
import com.example.letsridenew.models.Schedule

class DriversListAdapter(
    val context: Context, private val items: ArrayList<Schedule>,
    onClickListener: OnClickListener) :RecyclerView.Adapter<DriversListAdapter.ItemHolder>() {

    private var onClick : OnClickListener? = null

    init {
        this.onClick = onClickListener
    }

    interface OnClickListener{
        fun onClick(schedule: Schedule)
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(context).inflate(R.layout.schedule_list_items, parent, false)
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.title.text = items[position].getUser()!!.name
        val str = items[position].source!!.name+" to "+items[position].destination!!.name
        holder.subTitle.text = str

        holder.itemView.setOnClickListener {
            onClick!!.onClick(items[position])
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.list_txt1)
        val subTitle: TextView = itemView.findViewById(R.id.list_txt2)
    }

    public fun getEstimatedDriversFromList(scheduleList: ArrayList<Schedule>){

    }
}