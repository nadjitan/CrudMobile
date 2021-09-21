package com.example.crudmobile.ui.report

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.crudmobile.R
import com.example.crudmobile.db.IncidentReportDatabase
import kotlinx.android.synthetic.main.fragment_report.*

class ReportFragment : Fragment(R.layout.fragment_report) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            findNavController().navigate(
                ReportFragmentDirections.actionReportFragmentToReportsMonitorFragment()
            )
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val id = sharedPref?.getString("reportToView", null)

        val databaseHandler = IncidentReportDatabase(requireContext())
        val report = databaseHandler.getReport(id.toString())

        tvReportDateTime.text = report?.dateTime.toString()
        tvReportIncidentDescription.text = report?.incidentDescription
        tvReportIncidentType.text = report?.incidentType
        tvReportLocation.text = report?.location
        tvReportReporter.text = report?.reporter
        tvReportWitnesses.text = report?.witnesses?.joinToString()?.
                                 replace("[", "")?.replace("]", "")
        tvCreatedAt.text = report?.createdAt.toString()
    }
}