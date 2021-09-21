package com.example.crudmobile.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudmobile.R
import com.example.crudmobile.adapters.IncidentReportAdapter
import com.example.crudmobile.databinding.FragmentReportsMonitorBinding
import com.example.crudmobile.db.EmployeeDatabase
import com.example.crudmobile.db.IncidentReportDatabase
import com.example.crudmobile.models.Employee
import com.example.crudmobile.models.IncidentReport
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_reports_monitor.*

class ReportsMonitorFragment : Fragment(R.layout.fragment_reports_monitor) {

    private val incidentReportAdapter by lazy {
        IncidentReportAdapter(this, requireContext())
    }

    private var _binding: FragmentReportsMonitorBinding? = null
    private val binding get() = _binding!!

    private fun getDbIncidentReports(): List<IncidentReport> {
        val databaseHandler = IncidentReportDatabase(requireContext())
        return databaseHandler.getIncidentReports()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout for this fragment
        _binding = FragmentReportsMonitorBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                ReportsMonitorFragmentDirections.actionReportsMonitorFragmentToPagerFragment()
            )
        }

        // Populate IncidentReport RecyclerView
        val incidentReports = getDbIncidentReports()
        if (incidentReports.isNotEmpty()) {
            incidentReportAdapter.setData(incidentReports)

            binding.searchIncidentReport.visibility = View.VISIBLE
            binding.rvReportsMonitor.visibility = View.VISIBLE
            binding.tvNoReports.visibility = View.GONE

            binding.rvReportsMonitor.layoutManager = LinearLayoutManager(requireContext())
            binding.rvReportsMonitor.adapter = incidentReportAdapter
        } else {
            binding.searchIncidentReport.visibility = View.GONE
            binding.rvReportsMonitor.visibility = View.GONE
            binding.tvNoReports.visibility = View.VISIBLE
        }
        // IncidentReport SearchView
        binding.searchIncidentReport.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                incidentReportAdapter.getFilter().filter(query)
                return false
            }
        })

        return binding.root
    }

    /**
     * Delete alert dialog.
     * @param incidentReport Type of [IncidentReport]
     * @return void
     */
    fun deleteIncidentReportAlertDialog(incidentReport: IncidentReport) {
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setTitle("DELETE REPORT")
            .setMessage("Are you sure you want to delete ${incidentReport.reporter}'s report?")

        // Performing positive action
        builder.setNegativeButton("DELETE") { dialogInterface, _ ->

            val databaseHandler = IncidentReportDatabase(requireContext())

            val status = databaseHandler.deleteIncidentReport(incidentReport.id)

            if (status > -1) {
                Snackbar.make(
                    clReportsMonitor,
                    "${incidentReport.reporter}'s report deleted.",
                    Snackbar.LENGTH_SHORT
                ).show()

                incidentReportAdapter.setData(getDbIncidentReports())
            }

            dialogInterface.dismiss() // Dialog close
        }

        // Performing negative action
        builder.setPositiveButton("CANCEL") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog close
        }

        val alertDialog: AlertDialog = builder.create()

        // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.setCancelable(false)

        alertDialog.show()
    }
}