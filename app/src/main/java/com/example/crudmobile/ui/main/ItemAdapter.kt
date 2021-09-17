package com.example.crudmobile.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.crudmobile.R
import com.example.crudmobile.models.Employee
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter(
    private val context: Context,
    private val items: ArrayList<Employee>,
    private val fragment: MainFragment) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    /**
     * A [ViewHolder] describes an item view and metadata about
     * its place within the [RecyclerView].
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        val llMain: LinearLayout = view.llMain
        val tvId: TextView = view.tvId
        val tvName: TextView = view.tvName
        val tvEmail: TextView = view.tvEmail
        val ivEdit: ImageView = view.ivEdit
        val ivDelete: ImageView = view.ivDelete
    }

    /**
     * Inflates the item views which is designed in the XML layout file.
     *
     * Create a new [ViewHolder] and initializes some private fields
     * to be used by [RecyclerView].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_row, parent,false)
        )
    }

    /**
     * Binds each item in the [ArrayList] to a view.
     *
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     * This new [ViewHolder] should be constructed with a new [View] that can represent the items
     * of the given type. You can either create a new [View] manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        holder.tvId.text = item.id.toString()
        holder.tvName.text = item.name
        holder.tvEmail.text = item.email

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            holder.llMain.background = ContextCompat.getDrawable(context, R.drawable.border_blue)
            holder.tvEmail.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            holder.ivDelete.drawable.setTint(ContextCompat.getColor(context, R.color.navy_blue))
            holder.ivEdit.drawable.setTint(ContextCompat.getColor(context, R.color.navy_blue))
        } else {
            holder.llMain.background = null
            holder.tvId.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            holder.tvEmail.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            holder.ivDelete.drawable.setTint(ContextCompat.getColor(context, R.color.green_light))
            holder.ivEdit.drawable.setTint(ContextCompat.getColor(context, R.color.green_light))
        }

        holder.ivEdit.setOnClickListener {
            fragment.updateEmployeeDialog(item)
        }

        holder.ivDelete.setOnClickListener {
            fragment.deleteEmployeeAlertDialog(item)
        }
    }

    /**
     * Gets the number of items in the list.
     */
    override fun getItemCount(): Int {
        return items.size
    }
}