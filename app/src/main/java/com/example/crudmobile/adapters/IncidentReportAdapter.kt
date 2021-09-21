package com.example.crudmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.crudmobile.R
import com.example.crudmobile.databinding.IncidentReportRowLayoutBinding
import com.example.crudmobile.db.UserDatabase
import com.example.crudmobile.models.IncidentReport
import com.example.crudmobile.ui.auth.LoginFragmentDirections
import com.example.crudmobile.ui.report.ReportFormFragmentDirections
import com.example.crudmobile.ui.report.ReportsMonitorFragment
import com.example.crudmobile.ui.report.ReportsMonitorFragmentDirections
import com.example.crudmobile.utils.IncidentReportDiffUtil

class IncidentReportAdapter(
    private val fragment: ReportsMonitorFragment,
    private val context: Context
) : RecyclerView.Adapter<IncidentReportAdapter.ViewHolder>() {

    private var oldIncidentReportList = emptyList<IncidentReport>()
    private lateinit var incidentReportFilterList: List<IncidentReport>

    /**
     * A [ViewHolder] describes an item view and metadata about
     * its place within the [RecyclerView].
     */
    class ViewHolder(val binding: IncidentReportRowLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the item views which is designed in the XML layout file.
     *
     * Create a new [ViewHolder] and initializes some private fields
     * to be used by [RecyclerView].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                IncidentReportRowLayoutBinding.inflate(
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

        val item = incidentReportFilterList[position]
        val binding = holder.binding

        binding.tvId.text = item.id.toString()
        binding.tvRowReporter.text = item.reporter
        binding.tvRowIncidentReport.text = item.incidentType

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            binding.llIncidentReport.background = ContextCompat.getDrawable(context, R.drawable.bg_green_fill)
            binding.tvId.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.tvhRowReporter.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.tvRowReporter.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.tvhRowIncidentReport.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.tvRowIncidentReport.setTextColor(ContextCompat.getColor(context, R.color.navy_blue))
            binding.ivDelete.drawable.setTint(ContextCompat.getColor(context, R.color.navy_blue))
            binding.ivView.drawable.setTint(ContextCompat.getColor(context, R.color.navy_blue))
        } else {
            binding.llIncidentReport.background = null
            binding.tvId.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.tvhRowReporter.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.tvRowReporter.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.tvhRowIncidentReport.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.tvRowIncidentReport.setTextColor(ContextCompat.getColor(context, R.color.green_light))
            binding.ivDelete.drawable.setTint(ContextCompat.getColor(context, R.color.green_light))
            binding.ivView.drawable.setTint(ContextCompat.getColor(context, R.color.green_light))
        }

        binding.ivView.setOnClickListener {
            val sharedPref =
                fragment.activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            with (sharedPref.edit()) {
                putString("reportToView", item.id.toString())
                apply()
            }

            fragment.findNavController().navigate(
                ReportsMonitorFragmentDirections.actionReportsMonitorFragmentToReportFragment()
            )
        }

        binding.ivDelete.setOnClickListener {
            fragment.deleteIncidentReportAlertDialog(item)
        }
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
    override fun getItemCount() = incidentReportFilterList.size

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                incidentReportFilterList = if (charSearch.isEmpty()) {
                    oldIncidentReportList
                } else {
                    val resultList = ArrayList<IncidentReport>()

                    for (row in oldIncidentReportList) {
                        if (row.reporter.lowercase().contains(charSearch.lowercase()) ||
                            row.incidentType.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(row)
                        }
                    }

                    resultList
                }

                val filterResults = FilterResults()

                filterResults.values = incidentReportFilterList

                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                setFilterData(results?.values as List<IncidentReport>)
            }
        }
    }

    fun setData(newIncidentReportList: List<IncidentReport>) {
        val diffUtil = IncidentReportDiffUtil(oldIncidentReportList, newIncidentReportList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldIncidentReportList = newIncidentReportList

        incidentReportFilterList = oldIncidentReportList

        diffResults.dispatchUpdatesTo(this)
    }

    fun setFilterData(newIncidentReportList: List<IncidentReport>) {
        val diffUtil = IncidentReportDiffUtil(oldIncidentReportList, newIncidentReportList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        incidentReportFilterList = newIncidentReportList

        diffResults.dispatchUpdatesTo(this)
        notifyItemRangeChanged(0, incidentReportFilterList.size)
    }
}