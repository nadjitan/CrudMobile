package com.example.crudmobile.ui.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.crudmobile.R
import kotlinx.android.synthetic.main.fragment_report_form.*
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import androidx.core.content.ContextCompat
import com.example.crudmobile.db.IncidentReportDatabase
import com.example.crudmobile.models.IncidentReport
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class ReportFormFragment : Fragment(R.layout.fragment_report_form) {

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0

    private var incidentType = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            findNavController().navigate(
                ReportFormFragmentDirections.actionReportFormFragmentToPagerFragment()
            )
        }

        btn_date.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    "${year}-${monthOfYear + 1}-${dayOfMonth}".also { in_date.text = it }
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        btn_time.setOnClickListener {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(requireContext(),
                { _, hourOfDay, minute ->
                    "${checkDigit(hourOfDay)}:${checkDigit(minute)}".also { in_time.text = it }
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()
        }

        btnInjury.setOnClickListener {
            incidentType = "Injury"
            btnInjury.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.navy_blue)
            btnInjury.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.green_light))
            btnInjury.setIconTintResource(R.color.green_light)
            btnNearMiss.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnNearMiss.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnNearMiss.setIconTintResource(R.color.navy_blue)
            btnDeath.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnDeath.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnDeath.setIconTintResource(R.color.navy_blue)
        }
        btnNearMiss.setOnClickListener {
            incidentType = "Near Miss"
            btnInjury.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnInjury.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnInjury.setIconTintResource(R.color.navy_blue)
            btnNearMiss.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.navy_blue)
            btnNearMiss.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.green_light))
            btnNearMiss.setIconTintResource(R.color.green_light)
            btnDeath.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnDeath.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnDeath.setIconTintResource(R.color.navy_blue)
        }
        btnDeath.setOnClickListener {
            incidentType = "Death"
            btnInjury.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnInjury.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnInjury.setIconTintResource(R.color.navy_blue)
            btnNearMiss.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnNearMiss.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnNearMiss.setIconTintResource(R.color.navy_blue)
            btnDeath.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.navy_blue)
            btnDeath.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.green_light))
            btnDeath.setIconTintResource(R.color.green_light)
        }

        btnSubmitForm.setOnClickListener {
            saveIncidentReport()
        }
    }

    private fun saveIncidentReport() {
        val reporter = etReporter.text.toString()
        val dateTime = "$mYear-${mMonth + 1}-${mDay} $mHour:$mMinute"
        val incidentDescription = etIncidentDescription.text.toString()
        val witnesses = etWitnesses.text.toString()
        val location = etLocation.text.toString()

        val databaseHandler = IncidentReportDatabase(requireContext())

        if (reporter.isNotEmpty() && dateTime.isNotEmpty() &&
            dateTime.isNotEmpty() && incidentType.isNotEmpty() &&
            incidentDescription.isNotEmpty() && witnesses.isNotEmpty() &&
            location.isNotEmpty()) {

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            val status = databaseHandler.addIncidentReport(
                IncidentReport(0,
                    reporter = reporter,
                    dateTime = sdf.parse(dateTime),
                    incidentType = incidentType,
                    incidentDescription = incidentDescription,
                    witnesses = witnesses.split(",").map { it.trim() },
                    location = location
                )
            )

            if (status > -1) {
                Snackbar.make(
                    clIncidentReportForm,
                    "Incident Report saved.",
                    Snackbar.LENGTH_SHORT
                ).show()

                etReporter.text.clear()
                in_date.text = ""
                in_time.text = ""
                incidentType = ""
                etIncidentDescription.text.clear()
                etWitnesses.text.clear()
                etLocation.text.clear()
            }
        } else {
            Snackbar.make(
                clIncidentReportForm,
                "All fields must not be blank.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkDigit(number: Int) = if (number <= 9) "0$number" else number.toString()
}