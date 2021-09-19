package com.example.crudmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.crudmobile.R
import com.example.crudmobile.databinding.EmployeeRowLayoutBinding
import com.example.crudmobile.models.Employee
import com.example.crudmobile.ui.main.MainFragment
import com.example.crudmobile.utils.EmployeeDiffUtil
import kotlin.collections.ArrayList

class EmployeeAdapter(
    private val fragment: MainFragment,
    private val context: Context
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    private var oldEmployeeList = emptyList<Employee>()
    private lateinit var employeeFilterList: List<Employee>

    /**
     * A [ViewHolder] describes an item view and metadata about
     * its place within the [RecyclerView].
     */
    class ViewHolder(val binding: EmployeeRowLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the item views which is designed in the XML layout file.
     *
     * Create a new [ViewHolder] and initializes some private fields
     * to be used by [RecyclerView].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(EmployeeRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
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

        val item = employeeFilterList[position]
        val binding = holder.binding

        binding.tvId.text = item.id.toString()
        binding.tvName.text = item.name
        binding.tvEmail.text = item.email

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            binding.llMain.background = ContextCompat.getDrawable(context, R.drawable.bg_green_fill)
            binding.tvId.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.tvEmail.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.ivDelete.drawable.setTint(ContextCompat.getColor(context, R.color.navy_blue))
            binding.ivEdit.drawable.setTint(ContextCompat.getColor(context, R.color.navy_blue))
        } else {
            binding.llMain.background = null
            binding.tvId.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.tvEmail.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.ivDelete.drawable.setTint(ContextCompat.getColor(context, R.color.green_light))
            binding.ivEdit.drawable.setTint(ContextCompat.getColor(context, R.color.green_light))
        }

        binding.ivEdit.setOnClickListener {
            fragment.updateEmployeeDialog(item)
        }

        binding.ivDelete.setOnClickListener {
            fragment.deleteEmployeeAlertDialog(item)
        }
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
    override fun getItemCount() = employeeFilterList.size

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                employeeFilterList = if (charSearch.isEmpty()) {
                    oldEmployeeList
                } else {
                    val resultList = ArrayList<Employee>()

                    for (row in oldEmployeeList) {
                        if (row.name.lowercase().contains(charSearch.lowercase()) ||
                            row.email.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(row)
                        }
                    }

                    resultList
                }

                val filterResults = FilterResults()

                filterResults.values = employeeFilterList

                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                setFilterData(results?.values as List<Employee>)
            }
        }
    }

    fun setData(newEmployeeList: List<Employee>) {
        val diffUtil = EmployeeDiffUtil(oldEmployeeList, newEmployeeList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldEmployeeList = newEmployeeList

        employeeFilterList = oldEmployeeList

        diffResults.dispatchUpdatesTo(this)
    }

    fun setFilterData(newEmployeeList: List<Employee>) {
        val diffUtil = EmployeeDiffUtil(oldEmployeeList, newEmployeeList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        employeeFilterList = newEmployeeList

        diffResults.dispatchUpdatesTo(this)
        notifyItemRangeChanged(0, employeeFilterList.size)
    }
}