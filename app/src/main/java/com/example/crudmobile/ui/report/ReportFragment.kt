package com.example.crudmobile.ui.report

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.crudmobile.R
import com.example.crudmobile.db.IncidentReportDatabase
import com.example.crudmobile.models.IncidentReport
import com.example.crudmobile.ui.report.ReportFormFragment.Companion.checkDigit
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.btnBack
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment(R.layout.fragment_report) {

    private lateinit var report: IncidentReport

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val reportJson = sharedPref?.getString("reportToView", null)

        report = Gson().fromJson(reportJson, IncidentReport::class.java)

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        tvReportDateTime.text = report.dateTime.toString()
        tvReportIncidentDescription.text = report.incidentDescription
        tvReportIncidentType.text = report.incidentType
        tvReportLocation.text = report.location
        tvReportReporter.text = report.reporter
        tvReportWitnesses.text = report.witnesses.joinToString().
                                 replace("[", "").replace("]", "")
        tvReportPhoneNumber.text = report.phoneNumber.toString()
        tvCreatedAt.text = report.createdAt.toString()

        btnUpdateReport.setOnClickListener {
            llDateTime.visibility = View.GONE
            tvReportDateTime.visibility = View.GONE
            tvReportIncidentDescription.visibility = View.GONE
            tvReportIncidentType.visibility = View.GONE
            tvReportLocation.visibility = View.GONE
            tvReportReporter.visibility = View.GONE
            tvReportWitnesses.visibility = View.GONE
            tvReportPhoneNumber.visibility = View.GONE

            llDateTime.visibility = View.VISIBLE
            etReportIncidentDescription.visibility = View.VISIBLE
            etReportIncidentDescription.visibility = View.VISIBLE
            etReportIncidentDescription.setText(report.incidentDescription)
            etReportIncidentType.visibility = View.VISIBLE
            etReportIncidentType.setText(report.incidentType)
            etReportLocation.visibility = View.VISIBLE
            etReportLocation.setText(report.location)
            etReportReporter.visibility = View.VISIBLE
            etReportReporter.setText(report.reporter)
            etReportWitnesses.visibility = View.VISIBLE
            etReportWitnesses.setText(report.witnesses.joinToString().
                                replace("[", "").replace("]", ""))
            etReportPhoneNumber.visibility = View.VISIBLE
            etReportPhoneNumber.setText(report.phoneNumber.toString())

            btnUpdateContainer.visibility = View.VISIBLE
            btnUpdateReport.visibility = View.GONE
        }

        btnCancelUpdateReport.setOnClickListener {
            showTextViews()

            btnUpdateContainer.visibility = View.GONE
            btnUpdateReport.visibility = View.VISIBLE
        }

        btnSubmitUpdateReport.setOnClickListener {
            updateReport()
            btnUpdateContainer.visibility = View.GONE
            btnUpdateReport.visibility = View.VISIBLE
        }

        btnReportDate.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(),  R.style.DateTimePickerTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    "${year}-${monthOfYear + 1}-${dayOfMonth}".also { inReportDate.text = it }
                }, mYear, mMonth, mDay
            ).show()
        }

        btnReportTime.setOnClickListener {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            TimePickerDialog(requireContext(), R.style.DateTimePickerTheme,
                { _, hourOfDay, minute ->
                    "${checkDigit(hourOfDay)}:${checkDigit(minute)}".also { inReportTime.text = it }
                },
                mHour,
                mMinute,
                false
            ).show()
        }
    }

    private fun updateReport() {
        val db = IncidentReportDatabase(requireContext())
        val reporter = etReportReporter.text.toString().trim()
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                       .parse(inReportDate.text.toString() + " "
                               + inReportTime.text.toString())
        val description = etReportIncidentDescription.text.toString().trim()
        val incidentType = etReportIncidentType.text.toString().trim()
        val witnesses = etReportWitnesses.text.toString().trim()
                                                         .split(",").map { it.trim() }
        val location = etReportLocation.text.toString().trim()
        val phoneNumber = etReportPhoneNumber.text.toString().trim()

        val status = db.updateIncidentReport(
            IncidentReport(
                id = report.id,
                reporter = reporter,
                dateTime = dateTime,
                incidentDescription = description,
                incidentType = incidentType,
                witnesses = witnesses,
                location = location,
                phoneNumber = phoneNumber.toLong(),
                createdAt = report.createdAt
            )
        )

        // Update incident report success
        if (status > -1) {
            showTextViews()

            Snackbar.make(
                clReportFragment,
                "Incident report have been updated.",
                Snackbar.LENGTH_SHORT
            ).show()
            // Update TextViews
            val dbReport = db.getReport(report.id.toString())
            tvReportDateTime.text = dbReport?.dateTime.toString()
            tvReportIncidentDescription.text = dbReport?.incidentDescription
            tvReportIncidentType.text = dbReport?.incidentType
            tvReportLocation.text = dbReport?.location
            tvReportReporter.text = dbReport?.reporter
            tvReportWitnesses.text = dbReport?.witnesses?.joinToString()?.
            replace("[", "")?.replace("]", "")
            tvReportPhoneNumber.text = dbReport?.phoneNumber.toString()
        } else {
            Snackbar.make(
                clReportFragment,
                "Unable to update incident report.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun showTextViews() {
        llDateTime.visibility = View.VISIBLE
        tvReportDateTime.visibility = View.VISIBLE
        tvReportIncidentDescription.visibility = View.VISIBLE
        tvReportIncidentType.visibility = View.VISIBLE
        tvReportLocation.visibility = View.VISIBLE
        tvReportReporter.visibility = View.VISIBLE
        tvReportWitnesses.visibility = View.VISIBLE
        tvReportPhoneNumber.visibility = View.VISIBLE

        llDateTime.visibility = View.GONE
        etReportIncidentDescription.visibility = View.GONE
        etReportIncidentType.visibility = View.GONE
        etReportLocation.visibility = View.GONE
        etReportReporter.visibility = View.GONE
        etReportWitnesses.visibility = View.GONE
        etReportPhoneNumber.visibility = View.GONE
    }
}